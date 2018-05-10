package yaasl.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import yaasl.server.Broadcaster;
import yaasl.server.export.CSVExporter;
import yaasl.server.export.PDFExporter;
import yaasl.server.jsonapi.Element;
import yaasl.server.jsonapi.MultiData;
import yaasl.server.jsonapi.SingleData;
import yaasl.server.model.Flight;
import yaasl.server.model.Location;
import yaasl.server.model.Update;
import yaasl.server.persistence.CostSharingRepository;
import yaasl.server.persistence.FlightRepository;
import yaasl.server.persistence.LocationRepository;
import yaasl.server.providers.Ktrax;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.time.DateUtils.addDays;
import static org.apache.commons.lang3.time.DateUtils.addSeconds;
import static org.apache.commons.lang3.time.DateUtils.truncate;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;
import static org.springframework.web.bind.annotation.RequestMethod.*;
import static yaasl.server.convert.Converter.*;

@RestController
@RequestMapping("/rs/flights")
public class FlightsController {

    private Logger LOG = LoggerFactory.getLogger(getClass());

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private CostSharingRepository costSharingRepository;

    @Autowired
    private CSVExporter csvExporter;

    @Autowired
    private PDFExporter pdfExporter;

    @Autowired
    private Broadcaster broadcaster;

    @Autowired
    private Ktrax ktrax;

    private ObjectMapper objectMapper = new ObjectMapper();

    @ApiOperation(value = "getFlights", nickname = "getFlights")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = byte[].class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 422, message = "Unprocessable Entity"),
            @ApiResponse(code = 500, message = "Failure")})
    @RequestMapping(method = GET, produces = "application/vnd.api+json")
    public ResponseEntity<byte[]> getFlights(@RequestParam("filter[location]") Optional<String> location,
                                             @RequestParam("filter[date]") Optional<String> date,
                                             @RequestParam("format") Optional<String> format,
                                             @RequestParam("i18n") Optional<String> translations) {
        List<Flight> flights;
        if (location.isPresent() && date.isPresent()) {
            Location filterLocation = locationRepository.findByName(location.get().toUpperCase());
            Date filterDate = parseDate(date.get());
            if (filterLocation == null || filterDate == null) {
                return status(UNPROCESSABLE_ENTITY).build();
            }
            flights = flightRepository.findByLocationAndDate(filterLocation, filterDate, addDays(filterDate, 1));
            Date today = truncate(new Date(), DAY_OF_MONTH);
            flights = flights
                        .stream()
                        .filter(flight -> {
                            if (flight.getStartTime() == null) {
                                return today.equals(filterDate);
                            }
                            else {
                                return true;
                            }
                        })
                        .collect(toList());
        }
        else if (location.isPresent()) {
            Location filterLocation = locationRepository.findByName(location.get().toUpperCase());
            if (filterLocation == null) {
                return status(UNPROCESSABLE_ENTITY).build();
            }
            flights = flightRepository.findByLocation(filterLocation);
        }
        else {
            flights = flightRepository.findAllFlights();
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            byte[] data = null;
            if (!format.isPresent() || "application/vnd.api+json".equals(format.get())) {
                headers.add("Content-Type", "application/vnd.api+json");
                data = objectMapper.writeValueAsBytes(new MultiData(flights.stream().map(flight -> convert(flight)).collect(toList())));
            } else if (format.isPresent() && "csv".equals(format.get())) {
                headers.add("Content-Type", "text/csv");
                data = csvExporter.generate(flights);
            } else if (format.isPresent() && "pdf".equals(format.get())) {
                headers.add("Content-Type", "application/pdf");
                data = Base64.getEncoder().encode(pdfExporter.generate(flights, location, date, translations));
            }
            return new ResponseEntity<byte[]>(data, headers, OK);
        }
        catch (Exception e) {
            LOG.error("Error converting flights", e);
            return status(INTERNAL_SERVER_ERROR).build();
        }
    }

    @ApiOperation(value = "addFlight", nickname = "addFlight")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Failure")})
    @RequestMapping(method = POST)
    public ResponseEntity<SingleData> addFlight(@RequestBody SingleData data, @RequestHeader(value="X-Originator-ID") String originatorId) {
        try {
            Flight flight = convert(data.getData());
            flightRepository.save(flight);
            Update update = new Update("add", convert(flight));
            broadcaster.sendUpdate(update, originatorId);
            return ok(new SingleData(convert(flight)));
        }
        catch (Exception e) {
            LOG.error("Unable to add flight", e);
            return status(INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @ApiOperation(value = "updateFlight", nickname = "updateFlight")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Failure")})
    @RequestMapping(value="/{id}", method = PATCH)
    public ResponseEntity<SingleData> updateFlight(@PathVariable("id") Long id,
                                                   @RequestBody SingleData data,
                                                   @RequestHeader(value="X-Originator-ID") String originatorId,
                                                   HttpServletRequest request) {
        try {
            Flight incomingFlight = convert(data.getData());
            Flight flight = findFlight(incomingFlight.getId());
            if (flight != null) {
                if (!flight.isLocked() || request.isUserInRole("admin")) {
                    if (flight.getRevision() == incomingFlight.getRevision()) {
                        if (incomingFlight.isLocked()) {
                            incomingFlight.setEditable(false);
                        }
                        incomingFlight.setRevision(incomingFlight.getRevision() + 1);
                        flightRepository.save(incomingFlight);
                        Update update = new Update("update", convert(incomingFlight));
                        broadcaster.sendUpdate(update, originatorId);
                        return ok(new SingleData(convert(incomingFlight)));
                    }
                    else {
                        LOG.error("Update of flight rejected (user: {} revision: {} {})", request.getUserPrincipal().getName(), incomingFlight.getRevision(), flight.getRevision());
                        flight.setRevision(-flight.getRevision());
                        return ok(new SingleData(convert(flight)));
                    }
                }
                else {
                    return status(LOCKED).body(null);
                }
            }
            else {
                return status(NOT_FOUND).body(null);
            }
        }
        catch (Exception e) {
            LOG.error("Unable to update flight {}", id, e);
            return status(INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @ApiOperation(value = "deleteFlight", nickname = "deleteFlight")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Failure")})
    @RequestMapping(value="/{id}", method = DELETE)
    public ResponseEntity<SingleData> deleteFlight(@PathVariable("id") Long id, @RequestHeader(value="X-Originator-ID") String originatorId) {
        Optional<Flight> flight = flightRepository.findById(id);
        if (flight.isPresent()) {
            flightRepository.deleteById(id);
            Update update = new Update("delete", convert(flight.get()));
            broadcaster.sendUpdate(update, originatorId);
            LOG.debug("flight: {} deleted by originator {}", id, originatorId);
            return ok(new SingleData(convert(flight.get())));
        }
        else {
            return badRequest().body(null);
        }
    }

    @ApiOperation(value = "getCostSharings", nickname = "getCostSharings")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = MultiData.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Failure")})
    @RequestMapping(path = "/costSharings", method = GET, produces = "application/vnd.api+json")
    public MultiData getCostSharings() {
        List<Element> elements = new ArrayList<Element>();
        costSharingRepository
                .findAll()
                .forEach(costSharing -> elements.add(convert(costSharing)));
        return new MultiData(elements);
    }

    @ApiOperation(value = "ktrax", nickname = "ktrax")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Failure")})
    @RequestMapping(value="/ktrax", method = GET, produces = "application/json")
    public ResponseEntity<List<Flight>> getKtraxLogbook(@RequestParam("location") Optional<String> location, @RequestParam("date") Optional<String> date) {
        return ok(ktrax.getFlights(location.isPresent() ? location.get() : null, date.isPresent() ? date.get() : null));
    }

    private Flight findFlight(long id) {
        Optional<Flight> flight = flightRepository.findById(id);
        return flight.isPresent() ? flight.get() : null;
    }

 }
