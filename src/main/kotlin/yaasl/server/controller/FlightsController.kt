package yaasl.server.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import net.engio.mbassy.bus.MBassador
import org.apache.commons.lang3.time.DateUtils.addDays
import org.apache.commons.lang3.time.DateUtils.truncate
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RequestMethod.*
import yaasl.server.Broadcaster
import yaasl.server.convert.Converter.convert
import yaasl.server.convert.Converter.parseDate
import yaasl.server.event.FlightDeleted
import yaasl.server.export.CSVExporter
import yaasl.server.export.PDFExporter
import yaasl.server.jsonapi.MultiData
import yaasl.server.jsonapi.SingleData
import yaasl.server.model.Flight
import yaasl.server.model.Update
import yaasl.server.persistence.CostSharingRepository
import yaasl.server.persistence.FlightRepository
import yaasl.server.persistence.LocationRepository
import yaasl.server.providers.Ktrax
import java.util.*
import java.util.Calendar.DAY_OF_MONTH
import javax.servlet.http.HttpServletRequest
import kotlin.streams.toList

@RestController
@RequestMapping("/rs/flights")
class FlightsController(
        private val flightRepository: FlightRepository,
        private val locationRepository: LocationRepository,
        private val costSharingRepository: CostSharingRepository,
        private val csvExporter: CSVExporter,
        private val pdfExporter: PDFExporter,
        private val broadcaster: Broadcaster,
        private val ktrax: Ktrax,
        private val mBassador: MBassador<Any>) {

    private val LOG = LoggerFactory.getLogger(javaClass)
    private val objectMapper = ObjectMapper()

    @ApiOperation(value = "getCostSharings", nickname = "getCostSharings")
    @ApiResponses(value = arrayOf(ApiResponse(code = 200, message = "Success", response = MultiData::class)))
    @RequestMapping(path = arrayOf("/costSharings"), method = arrayOf(GET), produces = arrayOf("application/vnd.api+json"))
    fun costSharings(): MultiData {
        return MultiData(costSharingRepository.findAll().map { costSharing -> convert(costSharing) })
    }

    @ApiOperation(value = "getFlights", nickname = "getFlights")
    @ApiResponses(value = arrayOf(
            ApiResponse(code = 200, message = "Success", response = ByteArray::class),
            ApiResponse(code = 401, message = "Unauthorized"),
            ApiResponse(code = 403, message = "Forbidden"),
            ApiResponse(code = 404, message = "Not Found"),
            ApiResponse(code = 422, message = "Unprocessable Entity"),
            ApiResponse(code = 500, message = "Failure")))
    @RequestMapping(method = arrayOf(GET), produces = arrayOf("application/vnd.api+json"))
    fun getFlights(@RequestParam("filter[location]") location: Optional<String>,
                   @RequestParam("filter[date]") date: Optional<String>,
                   @RequestParam("format") format: Optional<String>,
                   @RequestParam("i18n") translations: Optional<String>): ResponseEntity<ByteArray> {
        var flights: List<Flight>
        if (location.isPresent && date.isPresent) {
            val filterLocation = locationRepository.findByIcao(location.get().toUpperCase())
            val filterDate = parseDate(date.get())
            if (filterLocation == null || filterDate == null) {
                return status(UNPROCESSABLE_ENTITY).build()
            }
            flights = flightRepository.findByLocationAndDate(filterLocation, filterDate, addDays(filterDate, 1))
            val today = truncate(Date(), DAY_OF_MONTH)
            flights = flights
                    .filter { flight ->
                        if (flight.startTime == null) {
                            today == filterDate
                        } else {
                            true
                        }
                    }
                    .toList()
        } else if (location.isPresent) {
            val filterLocation = locationRepository.findByIcao(location.get().toUpperCase())
            if (filterLocation == null) {
                return status(UNPROCESSABLE_ENTITY).build<ByteArray>()
            }
            flights = flightRepository.findByLocation(filterLocation)
        } else {
            flights = flightRepository.findAllFlights()
        }

        try {
            val headers = HttpHeaders()
            var data: ByteArray? = null
            if (!format.isPresent || "application/vnd.api+json" == format.get()) {
                headers.add(CONTENT_TYPE, "application/vnd.api+json")
                data = objectMapper.writeValueAsBytes(MultiData(flights.stream().map { flight -> convert(flight) }.toList()))
            } else if (format.isPresent && "csv" == format.get()) {
                headers.add(CONTENT_TYPE, "text/csv")
                data = csvExporter.generate(flights)
            } else if (format.isPresent && "pdf" == format.get()) {
                headers.add(CONTENT_TYPE, "application/pdf")
                data = Base64.getEncoder().encode(pdfExporter.generate(flights, location, date, translations))
            }
            return ResponseEntity<ByteArray>(data, headers, OK)
        } catch (e: Exception) {
            LOG.error("Error converting flights", e)
            return status(INTERNAL_SERVER_ERROR).build()
        }

    }

    @ApiOperation(value = "addFlight", nickname = "addFlight")
    @ApiResponses(value = arrayOf(
            ApiResponse(code = 200, message = "Success", response = ResponseEntity::class),
            ApiResponse(code = 500, message = "Failure")))
    @RequestMapping(method = arrayOf(POST))
    fun addFlight(@RequestBody data: SingleData, @RequestHeader(value = "X-Originator-ID") originatorId: String): ResponseEntity<SingleData> {
        try {
            val flight = convert<Flight>(data.data)!!
            flightRepository.save(flight)
            val update = Update("add", convert(flight))
            broadcaster.sendUpdate(update, originatorId)
            return ok(SingleData(convert(flight)))
        } catch (e: Exception) {
            LOG.error("Unable to add flight", e)
            return status(INTERNAL_SERVER_ERROR).body(null)
        }
    }

    @ApiOperation(value = "updateFlight", nickname = "updateFlight")
    @ApiResponses(value = arrayOf(
            ApiResponse(code = 200, message = "Success"),
            ApiResponse(code = 401, message = "Unauthorized"),
            ApiResponse(code = 403, message = "Forbidden"),
            ApiResponse(code = 404, message = "Not Found"),
            ApiResponse(code = 500, message = "Failure")))
    @RequestMapping(value = ["/{id}"], method = [PATCH])
    fun updateFlight(@PathVariable("id") id: Long?,
                     @RequestBody data: SingleData,
                     @RequestHeader(value = "X-Originator-ID") originatorId: String,
                     request: HttpServletRequest): ResponseEntity<SingleData> {
        try {
            val incomingFlight = convert<Flight>(data.data)
            if (incomingFlight != null) {
                val flight = findFlight(incomingFlight.id!!)
                if (flight != null) {
                    if (!flight.locked!! || request.isUserInRole("admin")) {
                        if (flight.revision == incomingFlight.revision) {
                            if (incomingFlight.locked!!) {
                                incomingFlight.editable = false
                            }
                            incomingFlight.revision = incomingFlight.revision!! + 1
                            flightRepository.save(incomingFlight)
                            val update = Update("update", convert(incomingFlight))
                            broadcaster.sendUpdate(update, originatorId)
                            return ok(SingleData(convert(incomingFlight)))
                        } else {
                            LOG.error("Update of flight rejected (user: {} revision: {} {})", request.userPrincipal.name, incomingFlight.revision, flight.revision)
                            flight.revision = -flight.revision!!
                            return ok(SingleData(convert(flight)))
                        }
                    } else {
                        return status(LOCKED).body(null)
                    }
                } else {
                    return status(NOT_FOUND).body(null)
                }
            }
        } catch (e: Exception) {
            LOG.error("Unable to update flight {}", id, e)
            return status(INTERNAL_SERVER_ERROR).body(null)
        }
        return status(BAD_REQUEST).body(null)
    }

    @ApiOperation(value = "deleteFlight", nickname = "deleteFlight")
    @ApiResponses(value = arrayOf(
            ApiResponse(code = 200, message = "Success"),
            ApiResponse(code = 401, message = "Unauthorized"),
            ApiResponse(code = 403, message = "Forbidden"),
            ApiResponse(code = 404, message = "Not Found"),
            ApiResponse(code = 500, message = "Failure")))
    @RequestMapping(value = ["/{id}"], method = [DELETE])
    fun deleteFlight(@PathVariable("id") id: Long, @RequestHeader(value = "X-Originator-ID") originatorId: String): ResponseEntity<SingleData> {
        val flight = flightRepository.findById(id)
        if (flight.isPresent) {
            flightRepository.deleteById(id)
            val update = Update("delete", convert(flight.get()))
            broadcaster.sendUpdate(update, originatorId)
            mBassador.publishAsync(FlightDeleted(flight.get()))
            LOG.debug("flight: {} deleted by originator {}", id, originatorId)
            return ok(SingleData(convert(flight.get())))
        } else {
            return badRequest().body(null)
        }
    }

    @ApiOperation(value = "lockFlights", nickname = "lockFlights")
    @ApiResponses(value = arrayOf(
            ApiResponse(code = 200, message = "Success"),
            ApiResponse(code = 401, message = "Unauthorized"),
            ApiResponse(code = 403, message = "Forbidden"),
            ApiResponse(code = 404, message = "Not Found"),
            ApiResponse(code = 500, message = "Failure")))
    @RequestMapping(value = ["/lock"], method = [POST])
    fun lockFlights(@RequestParam("location") locationName: Optional<String>, request: HttpServletRequest): ResponseEntity<*> {
        val location = if (locationName.isPresent) locationRepository.findByIcao(locationName.get()) else null
        if (location != null) {
            if (request.isUserInRole("fdl") || request.isUserInRole("admin")) {
                val today = truncate(Date(), DAY_OF_MONTH)
                val flights = flightRepository.findByLocationAndDate(location, today, addDays(today, 1))
                flights.forEach { flight ->
                    try {
                        flight.revision = flight.revision!! + 1
                        flight.locked = true
                        flight.editable = false
                        flightRepository.save(flight)
                        val update = Update("update", convert(flight))
                        broadcaster.sendUpdate(update)
                    } catch (e: Exception) {
                        LOG.error("Unable to lock flight {}", flight.id, e)
                    }
                }
                return ok<Nothing>(null)
            } else {
                return status(UNAUTHORIZED).body<Any>(null)
            }
        } else {
            return badRequest().body<Any>(null)
        }
    }

    @ApiOperation(value = "ktrax", nickname = "ktrax")
    @ApiResponses(value = arrayOf(
            ApiResponse(code = 200, message = "Success"),
            ApiResponse(code = 401, message = "Unauthorized"),
            ApiResponse(code = 403, message = "Forbidden"),
            ApiResponse(code = 404, message = "Not Found"),
            ApiResponse(code = 500, message = "Failure")))
    @RequestMapping(value = ["/ktrax"], method = [GET], produces = ["application/json"])
    fun getKtraxLogbook(@RequestParam("location") locationName: Optional<String>, @RequestParam("date") date: Optional<String>): ResponseEntity<List<Flight>> {
        if (locationName.isPresent) {
            val location = locationRepository.findByIcao(locationName.get())
            if (location != null) {
                return ok(ktrax.getFlights(location, if (date.isPresent) parseDate(date.get()) else null))
            }
        }
        return badRequest().body(null)
    }

    private fun findFlight(id: Long): Flight? {
        val flight = flightRepository.findById(id)
        return if (flight.isPresent) flight.get() else null
    }

}
