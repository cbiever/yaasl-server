package yaasl.server.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import yaasl.server.model.Flight;
import yaasl.server.model.Location;
import yaasl.server.persistence.AircraftRepository;
import yaasl.server.persistence.FlightRepository;
import yaasl.server.persistence.LocationRepository;
import yaasl.server.providers.Ktrax;

import java.util.Date;
import java.util.List;

import static java.lang.Integer.parseInt;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MINUTE;
import static org.apache.commons.lang3.time.DateUtils.*;
import static yaasl.server.convert.Converter.formatDate;

@Component
public class KtraxObserver {

    private Logger LOG = LoggerFactory.getLogger(getClass());

    @Autowired
    private Ktrax ktrax;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private AircraftRepository aircraftRepository;

    @Value("#{'${observer.locations}'.split(',')}")
    private List<String> locations;

    @Scheduled(cron = "${observer.cron}")
    private void observe() {
        Date today = new Date();
        if (locations != null) {
            locations.forEach(location -> {
                List<Flight> ktraxFlights = ktrax.getFlights(location, formatDate(today));
            });
        }
    }

    private List<Flight> getTodaysFlights(String airport) {
        Date today = truncate(new Date(), DAY_OF_MONTH);
        Location location = locationRepository.findByName(airport);
        return flightRepository.findByLocationAndDate(location, today, addDays(today, 1));
    }

}
