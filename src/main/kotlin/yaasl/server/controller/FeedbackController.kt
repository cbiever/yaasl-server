package yaasl.server.controller

import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.http.ResponseEntity.status
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.GET
import org.springframework.web.bind.annotation.RequestMethod.POST
import org.springframework.web.bind.annotation.RestController
import yaasl.server.Broadcaster
import yaasl.server.convert.Converter.convert
import yaasl.server.jsonapi.Element
import yaasl.server.jsonapi.MultiData
import yaasl.server.jsonapi.SingleData
import yaasl.server.model.Feedback
import yaasl.server.model.Update
import yaasl.server.persistence.FeedbackRepository

@RestController
@RequestMapping("/rs/feedbacks")
class FeedbackController(private val feedbackRepository: FeedbackRepository, private val broadcaster: Broadcaster) {

    private val LOG = LoggerFactory.getLogger(javaClass)

    @ApiOperation(value = "getFeedback", nickname = "getFeedback")
    @ApiResponses(value = arrayOf(
            ApiResponse(code = 200, message = "Success", response = Element::class)))
    @RequestMapping(method = arrayOf(GET), produces = arrayOf("application/vnd.api+json"))
    fun getFeedback(): ResponseEntity<MultiData> {
        val feedback = feedbackRepository.findAllFeedback()
        return ok(MultiData(feedback.map { f -> convert(f) }.toList()))
    }

    @ApiOperation(value = "addFeedback", nickname = "addFeedback")
    @ApiResponses(value = arrayOf(
            ApiResponse(code = 200, message = "Success", response = MultiData::class),
            ApiResponse(code = 500, message = "Failure")))
    @RequestMapping(method = arrayOf(POST))
    fun addFeedback(@RequestBody data: SingleData, @RequestHeader(value = "X-Originator-ID") originatorId: String): ResponseEntity<SingleData> {
        try {
            val feedback = convert<Feedback>(data.data)!!
            feedbackRepository.save(feedback)
            val update = Update("add", convert(feedback))
            broadcaster.sendUpdate(update, originatorId)
            return ok(SingleData(convert(feedback)))
        } catch (e: Exception) {
            LOG.error("Unable to add feedback", e)
            return status(INTERNAL_SERVER_ERROR).body(null)
        }
    }

}
