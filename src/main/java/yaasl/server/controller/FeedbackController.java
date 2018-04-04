package yaasl.server.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yaasl.server.Broadcaster;
import yaasl.server.jsonapi.Element;
import yaasl.server.jsonapi.MultiData;
import yaasl.server.jsonapi.SingleData;
import yaasl.server.model.Aircraft;
import yaasl.server.model.Feedback;
import yaasl.server.model.Flight;
import yaasl.server.model.Update;
import yaasl.server.persistence.AircraftRepository;
import yaasl.server.persistence.FeedbackRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static yaasl.server.convert.Converter.convert;

@RestController
@RequestMapping("/rs/feedbacks")
public class FeedbackController {

    private Logger LOG = LoggerFactory.getLogger(getClass());

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private Broadcaster broadcaster;

    @ApiOperation(value = "getFeedback", nickname = "getFeedback")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = Element.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Failure")})
    @RequestMapping(method = GET, produces = "application/vnd.api+json")
    public ResponseEntity<MultiData> getFeedback() {
        List<Feedback> feedback = feedbackRepository.findAllFeedback();
        return ok(new MultiData(feedback.stream().map(f-> convert(f)).collect(toList())));
    }

    @ApiOperation(value = "addFeedback", nickname = "addFeedback")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = MultiData.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Failure")})
    @RequestMapping(method = POST)
    public ResponseEntity<SingleData> addFeedback(@RequestBody SingleData data, @RequestHeader(value="X-Originator-ID") String originatorId) {
        try {
            Feedback feedback = convert(data.getData());
            feedbackRepository.save(feedback);
            Update update = new Update("add", convert(feedback));
            broadcaster.sendUpdate(update, originatorId);
            return ok(new SingleData(convert(feedback)));
        }
        catch (Exception e) {
            LOG.error("Unable to add feedback", e);
            return status(INTERNAL_SERVER_ERROR).body(null);
        }
    }

 }
