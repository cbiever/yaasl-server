package yaasl.server.export

import org.apache.commons.lang3.StringUtils.isNotEmpty
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import yaasl.server.model.Flight
import java.io.StringWriter
import java.text.SimpleDateFormat

@Component
class CSVExporter {

    private val LOG = LoggerFactory.getLogger(javaClass)

    fun generate(flights: List<Flight>): ByteArray {
        val writer = StringWriter()
        flights.forEach { flight -> addFlight(flight, writer) }
        return writer.toString().toByteArray(charset("UTF-8"))
    }

    private fun addFlight(flight: Flight, writer: StringWriter) {
        if (flight.aircraft != null) {
            writer.append(flight.aircraft!!.callSign)
        }
        writer.append(",")

        if (flight.pilot1 != null) {
            writer.append(flight.pilot1!!.name)
        }
        writer.append(",")

        if (flight.startTime != null) {
            writer.append(timeFormat.format(flight.startTime))
        }
        writer.append(",")

        if (flight.startLocation != null) {
            writer.append(flight.startLocation!!.icao)
        }
        writer.append(",")

        if (flight.landingTime != null) {
            writer.append(timeFormat.format(flight.landingTime))
        }
        writer.append(",")

        if (flight.landingLocation != null) {
            writer.append(flight.landingLocation!!.icao)
        }
        writer.append(",")

        if (flight.pilot1Role != null) {
            writer.append(flight.pilot1Role!!.description)
        }
        writer.append(",")

        if (flight.pilot2 != null) {
            writer.append(flight.pilot2!!.name)
        }
        writer.append(",")

        if (flight.pilot2Role != null) {
            writer.append(flight.pilot2Role!!.description)
        }
        writer.append(",")

        if (flight.towPlane != null) {
            writer.append(flight.towPlane!!.callSign)
        }
        writer.append(",")

        if (flight.towPilot != null) {
            writer.append(flight.towPilot!!.name)
        }
        writer.append(",")

        if (flight.towPlaneLandingTime != null) {
            writer.append(timeFormat.format(flight.towPlaneLandingTime))
        }
        writer.append(",")

        if (flight.costSharing != null) {
            writer.append(flight.costSharing!!.description)
        }
        writer.append(",")

        if (isNotEmpty(flight.comment)) {
            writer.append(flight.comment)
        }

        writer.append("\r\n")
    }

    companion object {

        private val timeFormat = SimpleDateFormat("HH:mm")
    }

}
