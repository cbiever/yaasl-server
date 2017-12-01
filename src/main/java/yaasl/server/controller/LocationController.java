package yaasl.server.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import yaasl.server.jsonapi.MultiData;
import yaasl.server.jsonapi.SingleData;
import yaasl.server.model.Location;
import yaasl.server.persistence.LocationRepository;

import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static yaasl.server.convert.Converter.convert;

@RestController
@RequestMapping("/rs/locations")
public class LocationController {

    private Logger LOG = LoggerFactory.getLogger(getClass());

    @Autowired
    private LocationRepository locationRepository;

    @ApiOperation(value = "getLocations", nickname = "getLocations")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = MultiData.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Failure")})
    @RequestMapping(method = GET, produces = "application/vnd.api+json")
    public MultiData getLocations(@RequestParam("filter[location]") Optional<String> locationFilter) {
        MultiData data = new MultiData();
        if (locationFilter.isPresent()) {
            Location location = locationRepository.findByName(locationFilter.get().toUpperCase());
            if (location != null) {
                data.getData().add(convert(location));
            }
        }
        else {
            locationRepository
                    .findAll()
                    .forEach(location -> data.getData().add(convert(location)));
        }
        return data;
    }

    @ApiOperation(value = "getLocation", nickname = "getLocation")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = MultiData.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Failure")})
    @RequestMapping(value="/{id}", method = GET, produces = "application/vnd.api+json")
    public SingleData getLocation(@PathVariable("id") Long id) {
        return new SingleData(convert(locationRepository.findOne(id)));
    }

 }
