package yaasl.server.providers

import org.apache.commons.collections4.CollectionUtils.isNotEmpty
import org.apache.commons.lang3.time.DateUtils.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.json.JsonParserFactory
import org.springframework.http.HttpStatus.OK
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import yaasl.server.convert.Converter.formatDate
import yaasl.server.model.Aircraft
import yaasl.server.model.Flight
import yaasl.server.model.Location
import java.lang.Integer.parseInt
import java.util.*
import java.util.Calendar.MINUTE
import java.util.Collections.emptyList

@Component
class Ktrax(@Value("\${provider.ktrax.url}") private val ktraxURL: String) {

    private val LOG = LoggerFactory.getLogger(javaClass)
    private val jsonParser = JsonParserFactory.getJsonParser()

    fun getFlights(location: Location?, date: Date?): List<Flight> {
        var url = ktraxURL + "?db=sortie&query_type=ap"
        if (location != null) {
            url += "&id=" + location.icao
        }
        if (date != null) {
            url += "&dbeg=" + formatDate(date) + "&dend=" + formatDate(date)
        }
        val restTemplate = RestTemplate()
        val response = restTemplate.getForEntity<String>(url, String::class.java)
        if (response.statusCode == OK) {
            return process(response.body, Date())
        } else {
            LOG.error("Unable to query Ktrax (http code: {})", response.statusCode)
            return emptyList()
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun process(json: String?, date: Date): List<Flight> {
        val data = jsonParser.parseMap(json)
        val sorties = data["sorties"] as List<Map<String, Any>>
        if (isNotEmpty(sorties)) {
            val flights = ArrayList<Flight>()
            val allFlights = ArrayDeque(sorties.map { sortie -> process(sortie, date) }.toList())
            while (!allFlights.isEmpty()) {
                val flight = allFlights.pop()
                val matchingFlight = findMatchingFlight(flight, allFlights)
                if (matchingFlight != null) {
                    if (flight.aircraft!!.canTow) {
                        matchingFlight.towPlane = flight.aircraft
                        matchingFlight.towPlaneLandingTime = flight.landingTime
                        flights.add(matchingFlight)
                    } else {
                        flight.towPlane = matchingFlight.aircraft
                        flight.towPlaneLandingTime = matchingFlight.landingTime
                        flights.add(flight)
                    }
                    allFlights.remove(matchingFlight)
                } else {
                    flights.add(flight)
                }
            }
            flights.sortBy { it.startTime }
            return flights
        } else {
            return emptyList()
        }
    }

    private fun findMatchingFlight(flight: Flight, allFlights: Deque<Flight>): Flight? {
        val towIdentifier = flight.aircraft!!.towIdentifier
        if (towIdentifier != null) {
            return allFlights
                    .filter { possibleMatchingFlight -> towIdentifier == possibleMatchingFlight.aircraft!!.identifier }
                    .firstOrNull()
        }
        return null
    }

    @Suppress("UNCHECKED_CAST")
    private fun process(sortie: Map<String, Any>, date: Date): Flight {
        val flight = Flight()

        flight.sequence = sortie["seq"] as Long

        val type = if (sortie["type"] != null) sortie["type"] as Int else 10
        val aircraft = Aircraft()
        aircraft.callSign = sortie["cs"] as String
        aircraft.competitionNumber = if ("-" != sortie["cn"]) sortie["cn"] as String else null
        aircraft.canTow = type == 2
        aircraft.needsTowing = type == 1
        aircraft.identifier = sortie["id"] as String
        aircraft.towIdentifier = sortie["tow_id"] as String
        flight.aircraft = aircraft

        val tkof = sortie["tkof"] as Map<String, Any>
        flight.startTime = prepareDate(tkof["time"] as String, date)
        val startLocation = Location()
        startLocation.icao = tkof["loc"] as String
        flight.startLocation = startLocation

        val ldg = sortie["ldg"] as Map<String, Any>
        flight.landingTime = prepareDate(ldg["time"] as String, date)
        val landingLocation = Location()
        landingLocation.icao = ldg["loc"] as String
        flight.landingLocation = landingLocation

        return flight
    }

    private fun prepareDate(time: String, date: Date): Date? {
        val hourAndMinutes = time.split(":".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
        return if (hourAndMinutes.size == 2) {
            truncate(setMinutes(setHours(date, parseInt(hourAndMinutes[0])), parseInt(hourAndMinutes[1])), MINUTE)
        } else {
            null
        }
    }

}
