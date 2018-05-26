package yaasl.server.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import yaasl.server.jsonapi.Element;
import yaasl.server.jsonapi.MultiData;
import yaasl.server.jsonapi.SingleData;
import yaasl.server.model.Location;
import yaasl.server.persistence.LocationRepository;

import java.util.ArrayList;
import java.util.List;
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
        List<Element> locations = new ArrayList<Element>();
        if (locationFilter.isPresent()) {
            Location location = locationRepository.findByIcao(locationFilter.get().toUpperCase());
            if (location != null) {
                locations.add(convert(location));
            }
        }
        else {
            locationRepository
                    .findAll()
                    .forEach(location -> locations.add(convert(location)));
        }
        return new MultiData(locations);
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
        Optional<Location> location = locationRepository.findById(id);
        return location.isPresent() ? new SingleData(convert(location.get())) : null;
    }

 }
