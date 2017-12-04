package yaasl.server.convert;

import yaasl.server.jsonapi.Element;
import yaasl.server.jsonapi.SingleData;
import yaasl.server.model.Aircraft;
import yaasl.server.model.Flight;
import yaasl.server.model.Location;
import yaasl.server.model.Pilot;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.lang.Long.parseLong;
import static java.util.Calendar.DATE;
import static java.util.Calendar.MINUTE;

public class Converter {

    private static DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_DATE_TIME;

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

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

    public static Element convert(Flight flight) {
        Element element = new Element();
        element.setId(flight.getId().toString());
        element.setType("flight");
        if (flight.getLocation() != null) {
            element.addRelationship("location", new SingleData(convert(flight.getLocation(), false)));
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
        if (flight.getPilot2() != null) {
            element.addRelationship("pilot2", new SingleData(convert(flight.getPilot2(), false)));
        }
        if (flight.getStartTime() != null) {
            element.addAttribute("start-time", dateTimeFormat.format(flight.getStartTime()));
        }
        if (flight.getLandingTime() != null) {
            element.addAttribute("landing-time", dateTimeFormat.format(flight.getLandingTime()));
        }
        return element;
    }

    public static Flight convert(Element element) throws Exception {
        Flight flight = new Flight();
        if (element.getId() != null) {
            flight.setId(parseLong(element.getId()));
        }
        flight.setLocation(convertLocation(getRelationship("location", element)));
        flight.setAircraft(convertAircraft(getRelationship("aircraft", element)));
        flight.setTowplane(convertAircraft(getRelationship("towplane", element)));
        flight.setPilot1(convertPilot(getRelationship("pilot1", element)));
        flight.setPilot2(convertPilot(getRelationship("pilot2", element)));
        Map<String, Object> attributes = element.getAttributes();
        if (attributes.get("start-time") != null) {
            flight.setStartTime(parseDateTime((String) attributes.get("start-time")));
        }
        if (attributes.get("landing-time") != null) {
            flight.setLandingTime(parseDateTime(((String) attributes.get("landing-time"))));
        }
        return flight;
    }

    public static Date parseDate(String date) {
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date addMinutes(Date date, int numberOfMinutes) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        calendar.add(MINUTE, numberOfMinutes);
        return calendar.getTime();
    }

    public static Date addDays(Date date, int numberOfDays) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        calendar.add(DATE, numberOfDays);
        return calendar.getTime();
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
            return null;
        }
    }

}
