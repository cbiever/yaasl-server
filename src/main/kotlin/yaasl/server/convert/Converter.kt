package yaasl.server.convert

import org.apache.commons.lang3.StringUtils.isNotEmpty
import org.slf4j.LoggerFactory
import yaasl.server.jsonapi.Element
import yaasl.server.jsonapi.SingleData
import yaasl.server.model.*
import java.lang.Long.parseLong
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object Converter {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    private val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

    private val LOG = LoggerFactory.getLogger(Converter::class.java)

    fun convert(location: Location, includeAttributes: Boolean = true): Element {
        val element = Element()
        element.setId(location.id.toString())
        element.setType("location")
        if (includeAttributes) {
            element.addAttribute("icao", location.icao!!)
            element.addAttribute("name", location.name!!)
        }
        return element
    }

    fun convert(aircraft: Aircraft, includeAttributes: Boolean = true): Element {
        val element = Element()
        element.setId(aircraft.id.toString())
        element.setType("aircraft")
        if (includeAttributes) {
            element.addAttribute("call-sign", aircraft.callSign)
            element.addAttribute("competition-number", aircraft.competitionNumber)
            element.addAttribute("can-tow", aircraft.canTow)
            element.addAttribute("needs-towing", aircraft.needsTowing)
            element.addAttribute("number-of-seats", aircraft.numberOfSeats)
        }
        return element
    }


    fun convert(pilot: Pilot, includeAttributes: Boolean = true): Element {
        val element = Element()
        element.setId(pilot.id.toString())
        element.setType("pilot")
        if (includeAttributes) {
            element.addAttribute("name", pilot.name!!)
            element.addAttribute("can-tow", pilot.isCanTow)
        }
        if (pilot.standardRole != null) {
            element.addRelationship("standard-role", SingleData(convert(pilot.standardRole!!, false)))
        }
        return element
    }

    fun convert(pilotRole: PilotRole, includeAttributes: Boolean = true): Element {
        val element = Element()
        element.setId(pilotRole.id.toString())
        element.setType("pilot-role")
        if (includeAttributes) {
            element.addAttribute("description", pilotRole.description!!)
            element.addAttribute("i18n", pilotRole.i18n!!)
        }
        return element
    }

    fun convert(costSharing: CostSharing, includeAttributes: Boolean = true): Element {
        val element = Element()
        element.setId(costSharing.id.toString())
        element.setType("cost-sharing")
        if (includeAttributes) {
            element.addAttribute("description", costSharing.description!!)
            element.addAttribute("i18n", costSharing.i18n!!)
        }
        return element
    }

    fun convert(flight: Flight): Element {
        val element = Element()
        element.setId(flight.id.toString())
        element.setType("flight")
        if (flight.startTime != null) {
            element.addAttribute("start-time", dateTimeFormat.format(flight.startTime))
        }
        if (flight.startLocation != null) {
            element.addRelationship("start-location", SingleData(convert(flight.startLocation!!, false)))
        }
        if (flight.landingTime != null) {
            element.addAttribute("landing-time", dateTimeFormat.format(flight.landingTime))
        }
        if (flight.landingLocation != null) {
            element.addRelationship("landing-location", SingleData(convert(flight.landingLocation!!, false)))
        }
        if (flight.aircraft != null) {
            element.addRelationship("aircraft", SingleData(convert(flight.aircraft!!, false)))
        }
        if (flight.pilot1 != null) {
            element.addRelationship("pilot1", SingleData(convert(flight.pilot1!!, false)))
        }
        if (flight.pilot1Role != null) {
            element.addRelationship("pilot1-role", SingleData(convert(flight.pilot1Role!!, false)))
        }
        if (flight.pilot2 != null) {
            element.addRelationship("pilot2", SingleData(convert(flight.pilot2!!, false)))
        }
        if (flight.pilot2Role != null) {
            element.addRelationship("pilot2-role", SingleData(convert(flight.pilot2Role!!, false)))
        }
        if (flight.towPlane != null) {
            element.addRelationship("tow-plane", SingleData(convert(flight.towPlane!!, false)))
        }
        if (flight.towPilot != null) {
            element.addRelationship("tow-pilot", SingleData(convert(flight.towPilot!!, false)))
        }
        if (flight.towPlaneLandingTime != null) {
            element.addAttribute("tow-plane-landing-time", dateTimeFormat.format(flight.towPlaneLandingTime))
        }
        if (flight.costSharing != null) {
            element.addRelationship("cost-sharing", SingleData(convert(flight.costSharing!!, false)))
        }
        if (isNotEmpty(flight.comment)) {
            element.addAttribute("comment", flight.comment!!)
        }
        element.addAttribute("editable", flight.editable)
        element.addAttribute("locked", flight.locked)
        element.addAttribute("revision", flight.revision)
        return element
    }

    fun convert(feedback: Feedback): Element {
        val element = Element()
        element.setId(feedback.id.toString())
        element.setType("feedback")
        if (feedback.feedback != null) {
            element.addAttribute("feedback", feedback.feedback!!)
        }
        if (feedback.comment != null) {
            element.addAttribute("comment", feedback.comment!!)
        }
        return element
    }

    fun <T> convert(element: Element): T? {
        val attributes = element.getAttributes()
        when (element.getType()) {
            "flights" -> {
                val flight = Flight()
                if (element.getId() != null) {
                    flight.id = parseLong(element.getId())
                }
                flight.startLocation = convertLocation(getRelationship("start-location", element))
                flight.startTime = parseDateTime(attributes["start-time"] as? String)
                flight.landingLocation = convertLocation(getRelationship("landing-location", element))
                flight.landingTime = parseDateTime(attributes["landing-time"] as? String)
                flight.aircraft = convertAircraft(getRelationship("aircraft", element))
                flight.pilot1 = convertPilot(getRelationship("pilot1", element))
                flight.pilot1Role = convertPilotRole(getRelationship("pilot1-role", element))
                flight.pilot2 = convertPilot(getRelationship("pilot2", element))
                flight.pilot2Role = convertPilotRole(getRelationship("pilot2-role", element))
                flight.towPlane = convertAircraft(getRelationship("tow-plane", element))
                flight.towPilot = convertPilot(getRelationship("tow-pilot", element))
                flight.towPlaneLandingTime = parseDateTime(attributes["tow-plane-landing-time"] as? String)
                flight.costSharing = convertCostSharing(getRelationship("cost-sharing", element))
                flight.comment = attributes["comment"] as? String
                flight.editable = getAttribute("editable", false, attributes)
                flight.locked = getAttribute("locked", true, attributes)
                flight.revision = getAttribute("revision", 0, attributes)?.toLong()
                return flight as T
            }
            "feedbacks" -> {
                val feedback = Feedback()
                feedback.feedback = getAttribute<String>("feedback", null, attributes)
                feedback.comment = getAttribute<String>("comment", null, attributes)
                return feedback as T
            }
        }
        return null
    }

    fun parseDate(date: String): Date? {
        try {
            return dateFormat.parse(date)
        } catch (e: ParseException) {
            return null
        }
    }

    fun formatDate(date: Date): String {
        return dateFormat.format(date)
    }

    private fun convertLocation(json: Map<String, Any>?): Location? {
        if (json != null) {
            val location = Location()
            location.id = parseLong((json["data"] as Map<String, Any>)["id"] as String)
            return location
        } else {
            return null
        }
    }

    private fun convertAircraft(json: Map<String, Any>?): Aircraft? {
        if (json != null) {
            val aircraft = Aircraft()
            aircraft.id = parseLong((json["data"] as Map<String, Any>)["id"] as String)
            return aircraft
        } else {
            return null
        }
    }

    private fun convertPilot(json: Map<String, Any>?): Pilot? {
        if (json != null) {
            val pilot = Pilot()
            pilot.id = parseLong((json["data"] as Map<String, Any>)["id"] as String)
            return pilot
        } else {
            return null
        }
    }

    private fun convertPilotRole(json: Map<String, Any>?): PilotRole? {
        if (json != null) {
            val pilotRole = PilotRole()
            pilotRole.id = parseLong((json["data"] as Map<String, Any>)["id"] as String)
            return pilotRole
        } else {
            return null
        }
    }

    private fun convertCostSharing(json: Map<String, Any>?): CostSharing? {
        if (json != null) {
            val costSharing = CostSharing()
            costSharing.id = parseLong((json["data"] as Map<String, Any>)["id"] as String)
            return costSharing
        } else {
            return null
        }
    }

    private fun getRelationship(name: String, element: Element): Map<String, Any>? {
        for ((key, value) in element.getRelationships() as Map<String, Any>) {
            if (name == key) {
                return value as Map<String, Any>
            }
        }
        return null
    }

    private fun parseDateTime(dateTime: String?): Date? {
        var date: Date? = null
        if (dateTime != null && dateTime.isNotEmpty()) {
            try {
                date = dateTimeFormat.parse(dateTime)
            } catch (e: ParseException) {
                LOG.error("Unable to convert {}", dateTime, e)
            }

        }
        return date
    }

    fun <T> getAttribute(attributeName: String, defaultValue: T?, attributes: Map<String, Any?>): T? {
        return if (attributes[attributeName] != null) {
            attributes[attributeName] as T
        } else {
            defaultValue
        }
    }

}
