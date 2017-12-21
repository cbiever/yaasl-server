package yaasl.server.convert;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yaasl.server.jsonapi.Element;
import yaasl.server.jsonapi.SingleData;
import yaasl.server.model.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.*;

import static java.lang.Long.parseLong;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
import static java.util.Calendar.*;
import static org.apache.commons.lang3.time.DateUtils.truncate;

public class Converter {

    private static DateTimeFormatter isoDateTimeFormat = ISO_DATE_TIME;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    private static Logger LOG = LoggerFactory.getLogger(Converter.class);

    public static Element convert(Location location) {
        return convert(location, true);
    }

    public static Element convert(Location location, boolean includeAttributes) {
        Element element = new Element();
        element.setId(location.getId().toString());
        element.setType("location");
        if (includeAttributes) {
            element.addAttribute("name", location.getName());
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
            element.addAttribute("can-tow", aircraft.isCanTow());
            element.addAttribute("number-of-seats", aircraft.getNumberOfSeats());
        }
        return element;
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

    public static Element convert(PilotRole pilotRole) {
        return convert(pilotRole, true);
    }

    public static Element convert(PilotRole pilotRole, boolean includeAttributes) {
        Element element = new Element();
        element.setId(pilotRole.getId().toString());
        element.setType("pilot-role");
        if (includeAttributes) {
            element.addAttribute("description", pilotRole.getDescription());
            element.addAttribute("i18n", pilotRole.getI18n());
        }
        return element;
    }

    public static Element convert(Flight flight) {
        Element element = new Element();
        element.setId(flight.getId().toString());
        element.setType("flight");
        if (flight.getStartTime() != null) {
            element.addAttribute("start-time", dateTimeFormat.format(flight.getStartTime()));
        }
        if (flight.getStartLocation() != null) {
            element.addRelationship("start-location", new SingleData(convert(flight.getStartLocation(), false)));
        }
        if (flight.getLandingTime() != null) {
            element.addAttribute("landing-time", dateTimeFormat.format(flight.getLandingTime()));
        }
        if (flight.getLandingLocation() != null) {
            element.addRelationship("landing-location", new SingleData(convert(flight.getLandingLocation(), false)));
        }
        if (flight.getAircraft() != null) {
            element.addRelationship("aircraft", new SingleData(convert(flight.getAircraft(), false)));
        }
        if (flight.getTowplane() != null) {
            element.addRelationship("towplane", new SingleData(convert(flight.getTowplane(), false)));
        }
        if (flight.getPilot1() != null) {
            element.addRelationship("pilot1", new SingleData(convert(flight.getPilot1(), false)));
        }
        if (flight.getPilot1Role() != null) {
            element.addRelationship("pilot1-role", new SingleData(convert(flight.getPilot1Role(), false)));
        }
        if (flight.getPilot2() != null) {
            element.addRelationship("pilot2", new SingleData(convert(flight.getPilot2(), false)));
        }
        if (flight.getPilot2Role() != null) {
            element.addRelationship("pilot2-role", new SingleData(convert(flight.getPilot2Role(), false)));
        }
        return element;
    }

    public static Flight convert(Element element) throws Exception {
        Map<String, Object> attributes = element.getAttributes();
        Flight flight = new Flight();
        if (element.getId() != null) {
            flight.setId(parseLong(element.getId()));
        }
        flight.setStartLocation(convertLocation(getRelationship("start-location", element)));
        if (attributes.get("start-time") != null) {
            flight.setStartTime(parseDateTime((String) attributes.get("start-time")));
        }
        flight.setLandingLocation(convertLocation(getRelationship("landing-location", element)));
        if (attributes.get("landing-time") != null) {
            flight.setLandingTime(parseDateTime(((String) attributes.get("landing-time"))));
        }
        flight.setAircraft(convertAircraft(getRelationship("aircraft", element)));
        flight.setTowplane(convertAircraft(getRelationship("towplane", element)));
        flight.setPilot1(convertPilot(getRelationship("pilot1", element)));
        flight.setPilot1Role(convertPilotRole(getRelationship("pilot1-role", element)));
        flight.setPilot2(convertPilot(getRelationship("pilot2", element)));
        flight.setPilot2Role(convertPilotRole(getRelationship("pilot2-role", element)));
        return flight;
    }

    public static Date parseDate(String date) {
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String formatDate(Date date) {
        return dateFormat.format(date);
    }

    private static Location convertLocation(Map<String, Object> json) throws IOException {
        if (json != null) {
            Location location = new Location();
            location.setId(parseLong((String) ((Map<String, Object>) json.get("data")).get("id")));
            return location;
        } else {
            return null;
        }
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

    private static PilotRole convertPilotRole(Map<String, Object> json) throws IOException {
        if (json != null) {
            PilotRole pilotRole = new PilotRole();
            pilotRole.setId(parseLong((String) ((Map<String, Object>) json.get("data")).get("id")));
            return pilotRole;
        } else {
            return null;
        }
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

    private static Date parseDateTime(String dateTime) {
        try {
            return dateTimeFormat.parse(dateTime);
        } catch (ParseException e) {
            LOG.error("Unable to convert {}", dateTime, e);
            return null;
        }
    }

}
