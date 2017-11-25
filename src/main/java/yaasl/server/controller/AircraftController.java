package yaasl.server.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yaasl.server.jsonapi.MultiData;
import yaasl.server.persistence.AircraftRepository;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static yaasl.server.convert.Converter.convert;

@RestController
@RequestMapping("/rs/aircrafts")
public class AircraftController {

    private Logger LOG = LoggerFactory.getLogger(getClass());

    @Autowired
    private AircraftRepository aircraftRepository;

    @ApiOperation(value = "getAircraft", nickname = "getAircraft")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = MultiData.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Failure")})
    @RequestMapping(method = GET, produces = "application/vnd.api+json")
    public MultiData getAircraft() {
        MultiData data = new MultiData();
        aircraftRepository
                .findAll()
                .forEach(aircraft -> data.getData().add(convert(aircraft)));
        return data;
    }

 }
