package yaasl.server.controller;

import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yaasl.server.Broadcaster;
import yaasl.server.jsonapi.MultiData;
import yaasl.server.jsonapi.SingleData;
import yaasl.server.model.Flight;
import yaasl.server.model.Location;
import yaasl.server.model.Update;
import yaasl.server.persistence.FlightsRepository;
import yaasl.server.persistence.LocationRepository;

import java.util.*;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.web.bind.annotation.RequestMethod.*;
import static yaasl.server.convert.Converter.addDays;
import static yaasl.server.convert.Converter.convert;
import static yaasl.server.convert.Converter.parseDate;

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

    @ApiOperation(value = "getFlights", nickname = "getFlights")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = MultiData.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 422, message = "Unprocessable Entity"),
            @ApiResponse(code = 500, message = "Failure")})
    @RequestMapping(method = GET, produces = "application/vnd.api+json")
    public ResponseEntity<MultiData> getFlights(@RequestParam("filter[location]") Optional<String> locationFilter, @RequestParam("filter[date]") Optional<String> dateFilter) {
        MultiData data = new MultiData();
        if (locationFilter.isPresent() && dateFilter.isPresent()) {
            Location location = locationRepository.findByName(locationFilter.get().toUpperCase());
            Date date = parseDate(dateFilter.get());
            if (date == null) {
                return ResponseEntity.status(UNPROCESSABLE_ENTITY).build();
            }
            List<Flight> flights = flightsRepository.findFlights(location, date, addDays(date, 1));
            flights.forEach(flight -> data.getData().add(convert(flight)));
        }
        else if (locationFilter.isPresent()) {
            Location location = locationRepository.findByName(locationFilter.get().toUpperCase());
            flightsRepository
                    .findByLocation(location)
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
            LOG.debug("flight: {}", flight);
            Update update = new Update("delete", convert(flight));
            broadcaster.sendUpdate(update, originatorId);
            return ResponseEntity.ok(new SingleData(convert(flight)));
        }
        else {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(null);
        }
    }

 }
