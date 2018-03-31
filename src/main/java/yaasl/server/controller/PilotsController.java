package yaasl.server.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yaasl.server.jsonapi.Element;
import yaasl.server.jsonapi.MultiData;
import yaasl.server.model.Pilot;
import yaasl.server.persistence.PilotRoleRepository;
import yaasl.server.persistence.PilotRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static yaasl.server.convert.Converter.convert;

@RestController
@RequestMapping("/rs/pilots")
public class PilotsController {

    private Logger LOG = LoggerFactory.getLogger(getClass());

    @Autowired
    private PilotRepository pilotRepository;

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
        List<Element> pilots = new ArrayList<Element>();
        pilotRepository
                .findAll()
                .forEach(pilot -> pilots.add(convert(pilot)));
        return new MultiData(pilots);
    }

    @ApiOperation(value = "getPilot", nickname = "getPilot")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = Element.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Failure")})
    @RequestMapping(value="/{id}", method = GET, produces = "application/vnd.api+json")
    public ResponseEntity<Element> getPilot(@PathVariable("id") Long id) {
        Optional<Pilot> pilot = pilotRepository.findById(id);
        if (pilot.isPresent()) {
            return ok(convert(pilot.get()));
        }
        else {
            return badRequest().body(null);
        }
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
        List<Element> pilotRoles = new ArrayList<Element>();
        pilotRoleRepository
                .findAll()
                .forEach(pilotRole -> pilotRoles.add(convert(pilotRole)));
        return new MultiData(pilotRoles);
    }

 }
