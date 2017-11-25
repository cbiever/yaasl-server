package yaasl.server.convert;

import yaasl.server.jsonapi.Element;
import yaasl.server.model.Aircraft;
import yaasl.server.model.Flight;
import yaasl.server.model.Pilot;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Long.parseLong;

public class Converter {

    private static DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_DATE_TIME;

    public static Element convert(Flight flight) {
        Element element = new Element();
        element.setId(flight.getId().toString());
        element.setType("flight");
        if (flight.getAircraft() != null) {
            element.addAttribute("aircraft", flight.getAircraft());
        }
        if (flight.getPilot1() != null) {
            element.addAttribute("pilot1", flight.getPilot1());
        }
        if (flight.getPilot2() != null) {
            element.addAttribute("pilot2", flight.getPilot2());
        }
        if (flight.getStartTime() != null) {
            element.addAttribute("start-time", flight.getStartTime().toString());
        }
        if (flight.getLandingTime() != null) {
            element.addAttribute("landing-time", flight.getLandingTime().toString());
        }
        return element;
    }

    public static Flight convert(Element data) throws Exception {
        Flight flight = new Flight();
        if (data.getId() != null) {
            flight.setId(parseLong(data.getId()));
        }
        Map<String, Object> attributes = data.getAttributes();
        if (attributes.get("aircraft") != null) {
            flight.setAircraft(convertAircraft((HashMap<String, Object>) attributes.get("aircraft")));
        }
        if (attributes.get("pilot1") != null) {
            flight.setPilot1(convertPilot((HashMap<String, Object>) attributes.get("pilot1")));
        }
        if (attributes.get("pilot2") != null) {
            flight.setPilot2(convertPilot((HashMap<String, Object>) attributes.get("pilot2")));
        }
        if (attributes.get("start-time") != null) {
            flight.setStartTime(OffsetDateTime.parse((String) attributes.get("start-time")));
        }
        if (attributes.get("landing-time") != null) {
            flight.setLandingTime(OffsetDateTime.parse((String) attributes.get("landing-time")));
        }
        return flight;
    }

    private static Aircraft convertAircraft(HashMap<String, Object> json) throws IOException {
        Aircraft aircraft = new Aircraft();
        if (json.get("_id") != null) {
            aircraft.setId(((Integer) json.get("_id")).longValue());
        }
        if (json.get("id") != null) {
            aircraft.setId(((Integer) json.get("id")).longValue());
        }
        aircraft.setCallSign((String) json.get("callSign"));
        aircraft.setNumberOfSeats((Integer) json.get("numberOfSeats"));
        return aircraft;
    }

    private static Pilot convertPilot(HashMap<String, Object> json) throws IOException {
        Pilot pilot = new Pilot();
        if (json.get("_id") != null) {
            pilot.setId(((Integer) json.get("_id")).longValue());
        }
        if (json.get("id") != null) {
            pilot.setId(((Integer) json.get("id")).longValue());
        }
        pilot.setName((String) json.get("name"));
        return pilot;
    }

    public static Element convert(Pilot pilot) {
        Element element = new Element();
        element.setId(pilot.getId().toString());
        element.setType("pilot");
        element.getAttributes().put("-id", pilot.getId());
        element.getAttributes().put("name", pilot.getName());
        return element;
    }

    public static Element convert(Aircraft aircraft) {
        Element element = new Element();
        element.setId(aircraft.getId().toString());
        element.setType("aircraft");
        element.getAttributes().put("-id", aircraft.getId());
        element.getAttributes().put("call-sign", aircraft.getCallSign());
        element.getAttributes().put("number-of-seats", aircraft.getNumberOfSeats());
        return element;
    }

}
