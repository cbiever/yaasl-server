package yaasl.server.providers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import yaasl.server.model.Aircraft;
import yaasl.server.model.Flight;
import yaasl.server.model.Location;

import java.util.*;

import static java.lang.Integer.parseInt;
import static java.util.Calendar.MINUTE;
import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.time.DateUtils.*;
import static org.springframework.http.HttpStatus.OK;
import static yaasl.server.convert.Converter.formatDate;
import static yaasl.server.convert.Converter.parseDate;

@Component
public class Ktrax {

    private Logger LOG = LoggerFactory.getLogger(getClass());

    @Value("${provider.ktrax.url}")
    private String ktraxURL;

    private JsonParser jsonParser = JsonParserFactory.getJsonParser();

    public List<Flight> getFlights(String location, String date) {
        String url = ktraxURL + "?db=sortie&query_type=ap";
        if (location != null) {
            url += "&id=" + location.toUpperCase();
        }
        if (date != null) {
            Date dateBegin = parseDate(date);
            url += "&dbeg=" + formatDate(dateBegin) + "&dend=" + formatDate(dateBegin);
        }
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        if (response.getStatusCode() == OK) {
            return process(response.getBody(), new Date());
        }
        else {
            LOG.error("Unable to query Ktrax (http code: {})", response.getStatusCode());
            return null;
        }
    }

    private List<Flight> process(String json, Date date) {
        Map<String, Object> data = jsonParser.parseMap(json);
        List<Map<String, Object>> sorties = (List<Map<String, Object>>) data.get("sorties");
        if (isNotEmpty(sorties)) {
            List<Flight> flights = new ArrayList<Flight>();
            Deque<Flight> allFlights = new ArrayDeque<Flight>(sorties.stream().map(sortie -> process(sortie, date)).collect(toList()));
            while (!allFlights.isEmpty()) {
                Flight flight = allFlights.pop();
                Flight matchingFlight = findMatchingFlight(flight, allFlights);
                if (matchingFlight != null) {
                    if (flight.getAircraft().isCanTow()) {
                        matchingFlight.setTowPlane(flight.getAircraft());
                        matchingFlight.setTowPlaneLandingTime(flight.getLandingTime());
                        flights.add(matchingFlight);
                    }
                    else {
                        flight.setTowPlane(matchingFlight.getAircraft());
                        flight.setTowPlaneLandingTime(matchingFlight.getLandingTime());
                        flights.add(flight);
                    }
                    allFlights.remove(matchingFlight);
                }
                else {
                    flights.add(flight);
                }
            }
            flights.sort(comparing(Flight::getStartTime));
            return flights;
        }
        else {
            return emptyList();
        }
    }

    private Flight findMatchingFlight(Flight flight, Deque<Flight> allFlights) {
        String towIdentifier = flight.getAircraft().getTowIdentifier();
        if (towIdentifier != null) {
            Optional<Flight> matchingFlight = allFlights
                    .stream()
                    .filter(possibleMatchingFlight -> towIdentifier.equals(possibleMatchingFlight.getAircraft().getIdentifier()))
                    .findFirst();
            return matchingFlight.isPresent() ? matchingFlight.get() : null;
        }
        return null;
    }

    private Flight process(Map<String, Object> sortie, Date date) {
        Flight flight = new Flight();

        int type = (sortie.get("type") != null ? (Integer) sortie.get("type") : 10);
        Aircraft aircraft = new Aircraft();
        aircraft.setCallSign((String) sortie.get("cs"));
        aircraft.setCompetitionNumber(!"-".equals(sortie.get("cn")) ? (String) sortie.get("cn") : null);
        aircraft.setCanTow(type == 2);
        aircraft.setNeedsTowing(type == 1);
        aircraft.setIdentifier((String) sortie.get("id"));
        aircraft.setTowIdentifier((String) sortie.get("tow_id"));
        flight.setAircraft(aircraft);

        Map<String, Object> tkof = (Map<String, Object>) sortie.get("tkof");
        flight.setStartTime(prepareDate((String) tkof.get("time"), date));
        Location startLocation = new Location();
        startLocation.setName((String) tkof.get("loc"));
        flight.setStartLocation(startLocation);

        Map<String, Object> ldg = (Map<String, Object>) sortie.get("ldg");
        flight.setLandingTime(prepareDate((String) ldg.get("time"), date));
        Location landingLocation = new Location();
        landingLocation.setName((String) ldg.get("loc"));
        flight.setLandingLocation(landingLocation);

        return flight;
    }

    private Date prepareDate(String time, Date date) {
        String[] hourAndMinutes = time.split(":");
        if (hourAndMinutes.length == 2) {
            return truncate(setMinutes(setHours(date, parseInt(hourAndMinutes[0])), parseInt(hourAndMinutes[1])), MINUTE);
        }
        else {
            return null;
        }
    }

}
