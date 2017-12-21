package yaasl.server.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import yaasl.server.Broadcaster;
import yaasl.server.jsonapi.MultiData;
import yaasl.server.jsonapi.SingleData;
import yaasl.server.model.Flight;
import yaasl.server.model.Location;
import yaasl.server.model.Update;
import yaasl.server.persistence.FlightsRepository;
import yaasl.server.persistence.LocationRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.HOUR;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.time.DateUtils.addDays;
import static org.apache.commons.lang3.time.DateUtils.truncate;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.web.bind.annotation.RequestMethod.*;
import static yaasl.server.convert.Converter.*;

@RestController
@RequestMapping("/rs/flights")
public class FlightsController {

    private Logger LOG = LoggerFactory.getLogger(getClass());

    @Autowired
    private FlightsRepository flightsRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private Broadcaster broadcaster;

    @Value("${provider.ktrax.url}")
    private String ktrax;

    @ApiOperation(value = "getFlights", nickname = "getFlights")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = MultiData.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 422, message = "Unprocessable Entity"),
            @ApiResponse(code = 500, message = "Failure")})
    @RequestMapping(method = GET, produces = "application/vnd.api+json")
    public ResponseEntity<MultiData> getFlights(@RequestParam("filter[location]") Optional<String> location,
                                                @RequestParam("filter[date]") Optional<String> date) {
        MultiData data = new MultiData();
        if (location.isPresent() && date.isPresent()) {
            Location filterLocation = locationRepository.findByName(location.get().toUpperCase());
            Date filterDate = parseDate(date.get());
            if (filterLocation == null || filterDate == null) {
                return ResponseEntity.status(UNPROCESSABLE_ENTITY).build();
            }
            List<Flight> flights = flightsRepository.findFlights(filterLocation, filterDate, addDays(filterDate, 1));
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
            flights.forEach(flight -> data.getData().add(convert(flight)));
        }
        else if (location.isPresent()) {
            Location filterLocation = locationRepository.findByName(location.get().toUpperCase());
            if (filterLocation == null) {
                return ResponseEntity.status(UNPROCESSABLE_ENTITY).build();
            }
            flightsRepository
                    .findByLocation(filterLocation)
                    .forEach(flight -> data.getData().add(convert(flight)));
        }
        else {
            flightsRepository
                    .findAll()
                    .forEach(flight -> data.getData().add(convert(flight)));
        }
        return ResponseEntity.ok(data);
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
            flightsRepository.save(flight);
            Update update = new Update("add", convert(flight));
            broadcaster.sendUpdate(update, originatorId);
            return ResponseEntity.ok(new SingleData(convert(flight)));
        }
        catch (Exception e) {
            LOG.error("Unable to add flight", e);
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(null);
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
    public ResponseEntity<SingleData> updateFlight(@PathVariable("id") Long id, @RequestBody SingleData data, @RequestHeader(value="X-Originator-ID") String originatorId) {
        try {
            Flight flight = convert(data.getData());
            flightsRepository.save(flight);
            Update update = new Update("update", convert(flight));
            broadcaster.sendUpdate(update, originatorId);
            return ResponseEntity.ok(new SingleData(convert(flight)));
        }
        catch (Exception e) {
            LOG.error("Unable to update flight {}", id, e);
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(null);
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
        Flight flight = flightsRepository.findOne(id);
        if (flight != null) {
            flightsRepository.delete(id);
            Update update = new Update("delete", convert(flight));
            broadcaster.sendUpdate(update, originatorId);
            LOG.debug("flight: {} deleted by originator {}", id, originatorId);
            return ResponseEntity.ok(new SingleData(convert(flight)));
        }
        else {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @ApiOperation(value = "ktrax", nickname = "ktrax")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Failure")})
    @RequestMapping(value="/ktrax", method = GET, produces = "application/json")
    public ResponseEntity<String> getKtraxLogbook(@RequestParam("location") Optional<String> location, @RequestParam("date") Optional<String> date) {
        String url = ktrax + "?db=sortie&query_type=ap";
        if (location.isPresent()) {
            url += "&id=" + location.get().toUpperCase();
        }
        if (date.isPresent()) {
            Date dateBegin = parseDate(date.get());
            Date dateEnd = addDays(dateBegin, 1);
            url += "&dbeg=" + formatDate(dateBegin) + "&dend=" + formatDate(dateEnd);
        }
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return ResponseEntity.ok(response.getBody());
    }

 }
