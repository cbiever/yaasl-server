package yaasl.server.controller

import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.GET
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import yaasl.server.convert.Converter.convert
import yaasl.server.jsonapi.MultiData
import yaasl.server.jsonapi.SingleData
import yaasl.server.persistence.LocationRepository
import java.util.*
import java.util.Arrays.asList

@RestController
@RequestMapping("/rs/locations")
class LocationController(val locationRepository: LocationRepository) {

    @ApiOperation(value = "getLocations", nickname = "getLocations")
    @ApiResponses(value = arrayOf(ApiResponse(code = 200, message = "Success", response = MultiData::class)))
    @RequestMapping(method = arrayOf(GET), produces = arrayOf("application/vnd.api+json"))
    fun getLocations(@RequestParam("filter[location]") locationFilter: Optional<String>): MultiData {
        if (locationFilter.isPresent) {
            val location = locationRepository.findByIcao(locationFilter.get().toUpperCase())
            if (location != null) {
                return MultiData(asList(convert(location)))
            }
        } else {
            return MultiData(locationRepository.findAll().map { location -> convert(location) })
        }
        return MultiData(emptyList())
    }

    @ApiOperation(value = "getLocation", nickname = "getLocation")
    @ApiResponses(value = arrayOf(ApiResponse(code = 200, message = "Success", response = SingleData::class)))
    @RequestMapping(value = ["/{id}"], method = arrayOf(GET), produces = arrayOf("application/vnd.api+json"))
    fun getLocation(@PathVariable("id") id: Long): SingleData? {
        val location = locationRepository.findById(id)
        return if (location.isPresent) SingleData(convert(location.get())) else null
    }

}
