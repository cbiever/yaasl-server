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
import yaasl.server.persistence.PilotRoleRepository;
import yaasl.server.persistence.PilotsRepository;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static yaasl.server.convert.Converter.convert;

@RestController
@RequestMapping("/rs/pilots")
public class PilotsController {

    private Logger LOG = LoggerFactory.getLogger(getClass());

    @Autowired
    private PilotsRepository pilotsRepository;

    @Autowired
    private PilotRoleRepository pilotRoleRepository;

    @ApiOperation(value = "getPilots", nickname = "getPilots")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = MultiData.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Failure")})
    @RequestMapping(method = GET, produces = "application/vnd.api+json")
    public MultiData getPilots() {
        MultiData data = new MultiData();
        pilotsRepository
                .findAll()
                .forEach(pilot -> data.getData().add(convert(pilot)));
        return data;
    }

    @ApiOperation(value = "getPilotRoles", nickname = "getPilotRoles")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = MultiData.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Failure")})
    @RequestMapping(path = "/roles", method = GET, produces = "application/vnd.api+json")
    public MultiData getPilotRoles() {
        MultiData data = new MultiData();
        pilotRoleRepository
                .findAll()
                .forEach(pilotRole -> data.getData().add(convert(pilotRole)));
        return data;
    }

 }
