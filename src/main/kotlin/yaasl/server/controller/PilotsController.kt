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
import org.springframework.web.bind.annotation.RestController
import yaasl.server.convert.Converter.convert
import yaasl.server.jsonapi.Element
import yaasl.server.jsonapi.MultiData
import yaasl.server.persistence.PilotRepository
import yaasl.server.persistence.PilotRoleRepository

@RestController
@RequestMapping("/rs/pilots")
class PilotsController(val pilotRepository: PilotRepository, val pilotRoleRepository: PilotRoleRepository) {

    @ApiOperation(value = "getPilots", nickname = "getPilots")
    @ApiResponses(value = arrayOf(ApiResponse(code = 200, message = "Success", response = MultiData::class)))
    @RequestMapping(method = arrayOf(GET), produces = arrayOf("application/vnd.api+json"))
    fun getPilots(): MultiData {
        return MultiData(pilotRepository.findAll().map { pilot -> convert(pilot) })
    }

    @ApiOperation(value = "getPilotRoles", nickname = "getPilotRoles")
    @ApiResponses(value = arrayOf(ApiResponse(code = 200, message = "Success", response = MultiData::class)))
    @RequestMapping(path = arrayOf("/roles"), method = arrayOf(GET), produces = arrayOf("application/vnd.api+json"))
    fun getPilotRoles(): MultiData {
        return MultiData(pilotRoleRepository.findAll().map { pilotRole -> convert(pilotRole) })
    }

    @ApiOperation(value = "getPilot", nickname = "getPilot")
    @ApiResponses(value = arrayOf(
            ApiResponse(code = 200, message = "Success", response = ResponseEntity::class),
            ApiResponse(code = 400, message = "Bad Request")))
    @RequestMapping(value = ["/{id}"], method = [GET], produces = ["application/vnd.api+json"])
    fun getPilot(@PathVariable("id") id: Long): ResponseEntity<Element> {
        val pilot = pilotRepository.findById(id)
        return if (pilot.isPresent) {
            ok(convert(pilot.get()))
        } else {
            badRequest().body(null)
        }
    }

}
