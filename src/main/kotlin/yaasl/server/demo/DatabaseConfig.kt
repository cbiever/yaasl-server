package yaasl.server.demo

import org.apache.commons.lang3.time.DateUtils.addDays
import org.apache.commons.lang3.time.DateUtils.addMinutes
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import yaasl.server.model.Aircraft
import yaasl.server.model.Flight
import yaasl.server.model.Pilot
import yaasl.server.model.TemporaryAuthority
import yaasl.server.persistence.*
import java.util.*
import javax.annotation.PostConstruct

@Configuration
@Profile("demo")
class DatabaseConfig(
        private val locationRepository: LocationRepository,
        private val pilotRepository: PilotRepository,
        private val aircraftRepository: AircraftRepository,
        private val flightRepository: FlightRepository,
        private val userRepository: UserRepository,
        private val authorityRepository: AuthorityRepository,
        private val temporaryAuthorityRepository: TemporaryAuthorityRepository
        ) {

    @PostConstruct
    fun initDatabase() {
        val now = Date()

        var flight = Flight()
        flight.startLocation = locationRepository.findByIcao("LSZW")
        flight.landingLocation = locationRepository.findByIcao("LSZW")
        flight.aircraft = getAircraft(1L)
        flight.pilot1 = getPilot(1L)
        flight.pilot2 = getPilot(2L)
        flight.startTime = addDays(now, -1)
        flight.landingTime = addMinutes(addDays(now, -1), 5)
        flight.editable = false
        flight.locked = true
        flightRepository.save(flight)

        flight = Flight()
        flight.startLocation = locationRepository.findByIcao("LSZW")
        flight.landingLocation = locationRepository.findByIcao("LSZW")
        flight.aircraft = getAircraft(2L)
        flight.pilot1 = getPilot(3L)
        flight.pilot2 = getPilot(4L)
        flight.startTime = now
        flight.landingTime = addMinutes(now, 5)
        flight.editable = true
        flight.locked = false
        flightRepository.save(flight)

        flight = Flight()
        flight.startLocation = locationRepository.findByIcao("LSZB")
        flight.landingLocation = locationRepository.findByIcao("LSZB")
        flight.aircraft = getAircraft(3L)
        flight.pilot1 = getPilot(5L)
        flight.pilot2 = getPilot(6L)
        flight.startTime = addMinutes(addDays(now, -1), 5)
        flight.landingTime = addMinutes(addDays(now, -1), 25)
        flight.editable = false
        flight.locked = true
        flightRepository.save(flight)

        flight = Flight()
        flight.startLocation = locationRepository.findByIcao("LSZB")
        flight.landingLocation = locationRepository.findByIcao("LSZB")
        flight.aircraft = getAircraft(4L)
        flight.pilot1 = getPilot(7L)
        flight.pilot2 = getPilot(8L)
        flight.startTime = now
        flight.landingTime = addMinutes(now, 5)
        flight.editable = true
        flight.locked = false
        flightRepository.save(flight)

        flight = Flight()
        flight.startLocation = locationRepository.findByIcao("LSTB")
        flight.landingLocation = locationRepository.findByIcao("LSTB")
        flight.aircraft = getAircraft(5L)
        flight.pilot1 = getPilot(7L)
        flight.pilot2 = getPilot(8L)
        flight.startTime = addMinutes(addDays(now, -1), 5)
        flight.landingTime = addMinutes(addDays(now, -1), 45)
        flight.editable = false
        flight.locked = true
        flightRepository.save(flight)

        flight = Flight()
        flight.startLocation = locationRepository.findByIcao("LSTB")
        flight.landingLocation = locationRepository.findByIcao("LSTB")
        flight.aircraft = getAircraft(6L)
        flight.pilot1 = getPilot(9L)
        flight.startTime = now
        flight.landingTime = addMinutes(now, 5)
        flight.editable = true
        flight.locked = false
        flightRepository.save(flight)

        val fdl = userRepository.findByUsername("fdl")
        val authority = authorityRepository.findByName("fdl")
        val temporaryFdlAuthority = TemporaryAuthority().apply { date = now; user = fdl; this.authority = authority }
        temporaryAuthorityRepository.save(temporaryFdlAuthority)
    }

    private fun getAircraft(id: Long): Aircraft? {
        val aircraft = aircraftRepository.findById(id)
        return if (aircraft.isPresent) aircraft.get() else null
    }

    private fun getPilot(id: Long): Pilot? {
        val pilot = pilotRepository.findById(id)
        return if (pilot.isPresent) pilot.get() else null
    }

}
