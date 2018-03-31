package yaasl.server.providers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import yaasl.server.model.Flight;
import yaasl.server.model.Location;
import yaasl.server.persistence.FlightRepository;
import yaasl.server.persistence.LocationRepository;

import java.util.*;

import static java.util.Calendar.DAY_OF_MONTH;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.time.DateUtils.addDays;
import static org.apache.commons.lang3.time.DateUtils.truncate;
import static org.springframework.http.HttpStatus.OK;
import static yaasl.server.convert.Converter.formatDate;

@Component
public class Ktrax {

    private Logger LOG = LoggerFactory.getLogger(getClass());

    @Value("${provider.ktrax.url}")
    private String ktrax;

    @Value("#{'${provider.ktrax.airports}'.split(',')}")
    private List<String> airports;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private LocationRepository locationRepository;

    private JsonParser jsonParser = JsonParserFactory.getJsonParser();

//    @Scheduled(cron = "${provider.ktrax.cron}")
    public void query() {
        if (airports != null) {
            airports.forEach(airport -> {
                Date now = new Date();
                String url = ktrax + "?db=sortie&query_type=ap&id=" + airport.toUpperCase() + "&dbeg=" + formatDate(now) + "&dend=" + formatDate(now);
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
                if (response.getStatusCode() == OK) {
                    process(response.getBody(), airport);
                }
                else {
                    LOG.error("Unable to query Ktrax (http code: {})", response.getStatusCode());
                }
            });
        }
    }

    private void process(String json, String airport) {
        Map<String, Object> data = jsonParser.parseMap(json);
        List<Map<String, Object>> sorties = (List<Map<String, Object>>) data.get("sorties");
        if (isNotEmpty(sorties)) {
            List<Flight> flights = getTodaysFlights(airport);
            sorties.forEach(sortie -> {
                String callsign = (String) sortie.get("cs");
                Map<String, Object> tkof = (Map<String, Object>) sortie.get("tkof");
                String takeoffTIme = (String) tkof.get("time");
                String takeoffAirport = (String) tkof.get("loc");
                Map<String, Object> ldg = (Map<String, Object>) sortie.get("ldg");
                String landingTime = (String) ldg.get("time");
                String landingAirport = (String) ldg.get("loc");
//                LOG.info("{} takeoff: {} {} landing: {} {}", callsign, takeoffAirport, takeoffTIme, landingAirport, landingTime);
            });
        }
    }

    private List<Flight> getTodaysFlights(String airport) {
        Date today = truncate(new Date(), DAY_OF_MONTH);
        Location location = locationRepository.findByName(airport);
        return flightRepository.findByLocationAndDate(location, today, addDays(today, 1));
    }

}
