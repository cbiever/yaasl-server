package yaasl.server.controller

import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.badRequest
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.GET
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import yaasl.server.convert.Converter.convert
import yaasl.server.jsonapi.Element
import yaasl.server.jsonapi.MultiData
import yaasl.server.persistence.AircraftRepository
import java.util.*
import java.util.Arrays.asList

@RestController
@RequestMapping("/rs/aircrafts")
class AircraftController(val aircraftRepository: AircraftRepository) {

    @ApiOperation(value = "getAircraft", nickname = "getAircraft")
    @ApiResponses(value = arrayOf(
            ApiResponse(code = 200, message = "Success", response = Element::class),
            ApiResponse(code = 400, message = "Bad Request")))
    @RequestMapping(value = ["/{id}"], method = [GET], produces = ["application/vnd.api+json"])
    fun getSingleAircraft(@PathVariable("id") id: Long): ResponseEntity<Element> {
        val aircraft = aircraftRepository.findById(id)
        return if (aircraft.isPresent) {
            ok(convert(aircraft.get()))
        } else {
            badRequest().body(null)
        }
    }

    @ApiOperation(value = "searchAircraft", nickname = "searchAircraft")
    @ApiResponse(code = 200, message = "Success", response = MultiData::class)
    @RequestMapping(method = arrayOf(GET), produces = arrayOf("application/vnd.api+json"))
    fun searchAircraft(@RequestParam("filter[callSign]") callSign: Optional<String>): MultiData {
        if (callSign.isPresent) {
            val aircraft = aircraftRepository.findAircraftByCallSign(callSign.get().toUpperCase())
            if (aircraft != null) {
                return MultiData(asList(convert(aircraft)))
            }
        } else {
            return MultiData(aircraftRepository.findAll().map { aircraft -> convert(aircraft) })
        }
        return MultiData(emptyList())
    }

}
