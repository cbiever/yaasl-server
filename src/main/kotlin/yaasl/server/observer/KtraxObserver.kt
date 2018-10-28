package yaasl.server.observer

import net.engio.mbassy.bus.MBassador
import net.engio.mbassy.listener.Handler
import net.engio.mbassy.listener.Listener
import net.engio.mbassy.listener.References
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.DateUtils.addDays
import org.apache.commons.lang3.time.DateUtils.truncate
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import yaasl.server.Broadcaster
import yaasl.server.convert.Converter.convert
import yaasl.server.event.FlightDeleted
import yaasl.server.model.Flight
import yaasl.server.model.Location
import yaasl.server.model.Update
import yaasl.server.persistence.AircraftRepository
import yaasl.server.persistence.FlightRepository
import yaasl.server.persistence.LocationRepository
import yaasl.server.providers.Ktrax
import java.util.*
import java.util.Calendar.DAY_OF_MONTH
import java.util.Calendar.MINUTE
import javax.annotation.PostConstruct

@Component
@Listener(references = References.Strong)
class KtraxObserver(
        private val ktrax: Ktrax,
        private val flightRepository: FlightRepository,
        private val locationRepository: LocationRepository,
        private val aircraftRepository: AircraftRepository,
        private val broadcaster: Broadcaster,
        private val mBassador: MBassador<*>,
        @Value("#{'\${observer.locations}'.split(',')}") private val locations: List<String>) {

    private val LOG = LoggerFactory.getLogger(javaClass)
    private val flights = ArrayList<Flight>()
    private val deletedSequences = ArrayList<Long>()

    @PostConstruct
    fun init() {
        mBassador.subscribe(this)
    }

    @Scheduled(cron = "\${observer.cron}")
    private fun observe() {
        val today = Date()
        locations.forEach { locationName ->
            val location = locationRepository.findByIcao(locationName)
            if (location != null) {
                val ktraxFlights = ktrax.getFlights(location, today)
                val flights = getTodaysFlights(location)
                ktraxFlights.forEach { ktraxFlight ->
                    if (!deletedSequences.contains(ktraxFlight.sequence)) {
                        var flight = findMatchingFlight(ktraxFlight, flights)
                        var newFlight = false
                        if (flight == null && ktraxFlight.aircraft != null) {
                            val aircraft = aircraftRepository.findAircraftByCallSign(ktraxFlight.aircraft!!.callSign!!)
                            if (aircraft != null) {
                                flight = Flight()
                                flight.aircraft = aircraft
                                flight.editable = true
                                newFlight = true
                            }
                        }
                        if (flight != null) {
                            val broadcast = merge(flight, ktraxFlight)
                            if (broadcast) {
                                val update = Update(if (newFlight) "add" else "update", convert(flight))
                                broadcaster.sendUpdate(update)
                            }
                        }
                    }
                }
            } else {
                LOG.warn("No location found with name {}", locationName)
            }
        }
    }

    private fun getTodaysFlights(location: Location): List<Flight> {
        val today = truncate(Date(), DAY_OF_MONTH)
        return flightRepository.findByLocationAndDate(location, today, addDays(today, 1))
    }

    private fun findMatchingFlight(ktraxFlight: Flight, flights: List<Flight>): Flight? {
        val matchingFlight = flights.stream()
                .filter { flight -> matches(flight, ktraxFlight) }
                .findFirst()
        return if (matchingFlight.isPresent) matchingFlight.get() else null
    }

    private fun matches(flight: Flight, ktraxFlight: Flight): Boolean {
        return if (sameAircraft(flight, ktraxFlight)) {
            if (flight.startTime == null) {
                true
            } else {
                truncate(flight.startTime, MINUTE) == truncate(ktraxFlight.startTime, MINUTE)
            }
        } else false
    }

    private fun sameAircraft(flight: Flight, ktraxFlight: Flight): Boolean {
        return if (flight.aircraft != null && ktraxFlight.aircraft != null) {
            StringUtils.equals(flight.aircraft!!.callSign, ktraxFlight.aircraft!!.callSign)
        } else {
            false
        }
    }

    private fun merge(flight: Flight, ktraxFlight: Flight): Boolean {
        var broadcast = false
        flight.sequence = ktraxFlight.sequence
        if (flight.startLocation == null && ktraxFlight.startLocation != null) {
            val startingLocation = locationRepository.findByIcao(ktraxFlight.startLocation!!.icao!!)
            if (startingLocation != null) {
                flight.startLocation = startingLocation
                broadcast = true
            } else {
                LOG.warn("No start location found with name {}", ktraxFlight.startLocation!!.icao)
            }
        }
        if (flight.startTime == null && ktraxFlight.startTime != null) {
            flight.startTime = ktraxFlight.startTime
            broadcast = true
        }
        if (flight.landingLocation == null && ktraxFlight.landingLocation != null) {
            val landingLocation = locationRepository.findByIcao(ktraxFlight.landingLocation!!.icao!!)
            if (landingLocation != null) {
                flight.landingLocation = landingLocation
                broadcast = true
            } else {
                LOG.warn("No location found with name {}", ktraxFlight.landingLocation!!.icao!!)
            }
        }
        if (flight.landingTime == null && ktraxFlight.landingTime != null) {
            flight.landingTime = ktraxFlight.landingTime
            broadcast = true
        }
        if (flight.towPlane == null && ktraxFlight.towPlane != null) {
            val towPlane = aircraftRepository.findAircraftByCallSign(ktraxFlight.towPlane!!.callSign!!)
            if (towPlane != null) {
                flight.towPlane = towPlane
                broadcast = true
            } else {
                LOG.warn("No tow plane found with call sign {}", ktraxFlight.towPlane!!.callSign)
            }
        }
        if (flight.towPlaneLandingTime == null && ktraxFlight.towPlaneLandingTime != null) {
            flight.towPlaneLandingTime = ktraxFlight.towPlaneLandingTime
            broadcast = true
        }
        if (broadcast) {
            flights.add(flightRepository.save(flight))
        }
        return broadcast
    }

    @Scheduled(cron = "0 0 0 * * *")
    fun clearFlightsAndDeletedSequences() {
        flights.clear()
        deletedSequences.clear()
        LOG.info("Deleted flights cleared")
    }

    @Handler
    fun handle(flightDeleted: FlightDeleted) {
        var flight = flights.find { it.id == flightDeleted.flight.id }
        if (flight != null) {
            deletedSequences.add(flight.sequence)
        }
    }

}
