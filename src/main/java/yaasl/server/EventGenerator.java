package yaasl.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import yaasl.server.model.Flight;
import yaasl.server.model.Update;
import yaasl.server.persistence.FlightsRepository;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.time.DateUtils.addMinutes;
import static yaasl.server.convert.Converter.convert;

@Component
public class EventGenerator {

    private Logger LOG = LoggerFactory.getLogger(getClass());

    @Autowired
    private Broadcaster broadcaster;

    @Autowired
    private FlightsRepository flightsRepository;

//    @Scheduled(fixedRate = 30000)
    public void generateEvent() {
        List<Flight> flights = getAllFlights();
        Date now = new Date();

        if (Math.random() < 0.3) {
            List<Flight> allFlightsWithoutStartTime = flights
                    .stream()
                    .filter(flight -> flight.getStartTime() == null)
                    .collect(toList());
            if (!allFlightsWithoutStartTime.isEmpty()) {
                int i = (int) Math.round(Math.random() * (allFlightsWithoutStartTime.size() - 1));
                Flight flight = allFlightsWithoutStartTime.get(i);
                flight.setStartTime(now);
                flightsRepository.save(flight);
                Update update = new Update("update", convert(flight));
                broadcaster.sendUpdate(update, null);
                LOG.info("Start time set for flight: {}", flight.getId());
            }
        }

        if (Math.random() < 0.2) {
            List<Flight> allFlightsWithoutLandingTime = flights
                    .stream()
                    .filter(flight -> flight.getStartTime() != null && flight.getLandingTime() == null)
                    .collect(toList());
            if (!allFlightsWithoutLandingTime.isEmpty()) {
                int i = (int) Math.round(Math.random() * (allFlightsWithoutLandingTime.size() - 1));
                Flight flight = allFlightsWithoutLandingTime.get(i);
                if (flight.getStartTime().before(addMinutes(now, -4))) {
                    flight.setLandingTime(now);
                    flightsRepository.save(flight);
                    Update update = new Update("update", convert(flight));
                    broadcaster.sendUpdate(update, null);
                    LOG.info("Landing time set for flight: {}", flight.getId());
                    return;
                }
            }
        }
    }

    private List<Flight> getAllFlights() {
        List<Flight> flights = new ArrayList<>();
        for (Flight flight: flightsRepository.findAll()) {
            flights.add(flight);
        }
        return flights;
    }

}
