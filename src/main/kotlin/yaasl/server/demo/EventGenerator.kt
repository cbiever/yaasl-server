package yaasl.server.demo

import org.apache.commons.lang3.time.DateUtils.addMinutes
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import yaasl.server.Broadcaster
import yaasl.server.convert.Converter.convert
import yaasl.server.model.Flight
import yaasl.server.model.Update
import yaasl.server.persistence.FlightRepository
import java.util.*

@Profile("demo")
class EventGenerator(private val broadcaster: Broadcaster, private val flightRepository: FlightRepository) {

    private val LOG = LoggerFactory.getLogger(javaClass)

    private val allFlights: List<Flight>
        get() {
            val flights = ArrayList<Flight>()
            for (flight in flightRepository.findAll()) {
                flights.add(flight)
            }
            return flights
        }

    @Scheduled(fixedRate = 30000)
    fun generateEvent() {
        val flights = allFlights
        val now = Date()

        if (Math.random() < 0.3) {
            val allFlightsWithoutStartTime = flights
                    .filter { flight -> flight.startTime == null }
                    .toList()
            if (!allFlightsWithoutStartTime.isEmpty()) {
                val i = Math.round(Math.random() * (allFlightsWithoutStartTime.size - 1)).toInt()
                val flight = allFlightsWithoutStartTime[i]
                flight.startTime = now
                flightRepository.save(flight)
                val update = Update("update", convert(flight))
                broadcaster.sendUpdate(update, null)
                LOG.info("Start time set for flight: {}", flight.id)
            }
        }

        if (Math.random() < 0.2) {
            val allFlightsWithoutLandingTime = flights
                    .filter { flight -> flight.startTime != null && flight.landingTime == null }
                    .toList()
            if (!allFlightsWithoutLandingTime.isEmpty()) {
                val i = Math.round(Math.random() * (allFlightsWithoutLandingTime.size - 1)).toInt()
                val flight = allFlightsWithoutLandingTime[i]
                if (flight.startTime!!.before(addMinutes(now, -4))) {
                    flight.landingTime = now
                    flightRepository.save(flight)
                    val update = Update("update", convert(flight))
                    broadcaster.sendUpdate(update, null)
                    LOG.info("Landing time set for flight: {}", flight.id)
                    return
                }
            }
        }
    }

}
