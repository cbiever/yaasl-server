package yaasl.server.convert;

import com.fasterxml.jackson.databind.ObjectMapper;
import yaasl.server.jsonapi.Element;
import yaasl.server.jsonapi.SingleData;
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
            element.addRelationship("aircraft", new SingleData(convert(flight.getAircraft(), false)));
        }
        if (flight.getPilot1() != null) {
            element.addRelationship("pilot1", new SingleData(convert(flight.getPilot1(), false)));
        }
        if (flight.getPilot2() != null) {
            element.addRelationship("pilot2", new SingleData(convert(flight.getPilot2(), false)));
        }
        if (flight.getStartTime() != null) {
            element.addAttribute("start-time", flight.getStartTime().toString());
        }
        if (flight.getLandingTime() != null) {
            element.addAttribute("landing-time", flight.getLandingTime().toString());
        }
        return element;
    }

    public static Flight convert(Element element) throws Exception {
        Flight flight = new Flight();
        if (element.getId() != null) {
            flight.setId(parseLong(element.getId()));
        }
        flight.setAircraft(convertAircraft(getRelationship("aircraft", element)));
        flight.setPilot1(convertPilot(getRelationship("pilot1", element)));
        flight.setPilot2(convertPilot(getRelationship("pilot2", element)));
        Map<String, Object> attributes = element.getAttributes();
        if (attributes.get("start-time") != null) {
            flight.setStartTime(OffsetDateTime.parse((String) attributes.get("start-time")));
        }
        if (attributes.get("landing-time") != null) {
            flight.setLandingTime(OffsetDateTime.parse((String) attributes.get("landing-time")));
        }
        return flight;
    }

    private static Aircraft convertAircraft(Map<String, Object> json) throws IOException {
        if (json != null) {
            Aircraft aircraft = new Aircraft();
            aircraft.setId(parseLong((String) ((Map<String, Object>) json.get("data")).get("id")));
            return aircraft;
        } else {
            return null;
        }
    }

    private static Pilot convertPilot(Map<String, Object> json) throws IOException {
        if (json != null) {
            Pilot pilot = new Pilot();
            pilot.setId(parseLong((String) ((Map<String, Object>) json.get("data")).get("id")));
            return pilot;
        } else {
            return null;
        }
    }

    public static Element convert(Pilot pilot) {
        return convert(pilot, true);
    }

    public static Element convert(Pilot pilot, boolean includeAttributes) {
        Element element = new Element();
        element.setId(pilot.getId().toString());
        element.setType("pilot");
        if (includeAttributes) {
            element.addAttribute("name", pilot.getName());
        }
        return element;
    }

    public static Element convert(Aircraft aircraft) {
        return convert(aircraft, true);
    }

    public static Element convert(Aircraft aircraft, boolean includeAttributes) {
        Element element = new Element();
        element.setId(aircraft.getId().toString());
        element.setType("aircraft");
        if (includeAttributes) {
            element.addAttribute("call-sign", aircraft.getCallSign());
            element.addAttribute("number-of-seats", aircraft.getNumberOfSeats());
        }
        return element;
    }

    private static Map<String, Object> getRelationship(String name, Element element) {
        if (element.getRelationships() != null) {
            for (Map.Entry<String, Object> relationship : element.getRelationships().entrySet()) {
                if (name.equals(relationship.getKey())) {
                    return (Map<String, Object>) relationship.getValue();
                }
            }
        }
        return null;
    }

}
