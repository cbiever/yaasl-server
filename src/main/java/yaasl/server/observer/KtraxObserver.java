package yaasl.server.observer;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;
import net.engio.mbassy.listener.References;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import yaasl.server.Broadcaster;
import yaasl.server.event.FlightDeleted;
import yaasl.server.model.Aircraft;
import yaasl.server.model.Flight;
import yaasl.server.model.Location;
import yaasl.server.model.Update;
import yaasl.server.persistence.AircraftRepository;
import yaasl.server.persistence.FlightRepository;
import yaasl.server.persistence.LocationRepository;
import yaasl.server.providers.Ktrax;

import javax.annotation.PostConstruct;
import java.util.*;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MINUTE;
import static org.apache.commons.lang3.time.DateUtils.addDays;
import static org.apache.commons.lang3.time.DateUtils.truncate;
import static yaasl.server.convert.Converter.convert;

@Component
@Listener(references = References.Strong)
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

    @Autowired
    private Broadcaster broadcaster;

    @Autowired
    private MBassador mBassador;

    private List<Flight> flights = new ArrayList<Flight>();
    private List<Integer> deletedSequences = new ArrayList<Integer>();

    @Value("#{'${observer.locations}'.split(',')}")
    private List<String> locations;

    @PostConstruct
    public void init() {
        mBassador.subscribe(this);
    }

//    @Scheduled(cron = "${observer.cron}")
@Scheduled(cron = "0 */1 * * * *")
    private void observe() {
        Date today = new Date();
        if (locations != null) {
            locations.forEach(locationName -> {
                Location location = locationRepository.findByIcao(locationName);
                if (location != null) {
                    List<Flight> ktraxFlights = ktrax.getFlights(location, today);
                    List<Flight> flights = getTodaysFlights(location);
                    ktraxFlights.forEach(ktraxFlight -> {
                        if (!deletedSequences.contains(ktraxFlight.getSequence())) {
                            Flight flight = findMatchingFlight(ktraxFlight, flights);
                            if (flight == null && ktraxFlight.getAircraft() != null) {
                                Aircraft aircraft = aircraftRepository.findAircraftByCallSign(ktraxFlight.getAircraft().getCallSign());
                                if (aircraft != null) {
                                    flight = new Flight();
                                    flight.setAircraft(aircraft);
                                    flight.setEditable(true);
                                }
                            }
                            if (flight != null) {
                                boolean broadcast = merge(flight, ktraxFlight);
                                if (broadcast) {
                                    Update update = new Update("update", convert(flight));
                                    broadcaster.sendUpdate(update);
                                }
                            }
                        }
else {
    LOG.info("flight {} ignored", ktraxFlight.getSequence());
}
                    });
                }
                else {
                    LOG.warn("No location found with name {}", locationName);
                }
            });
        }
    }

    private List<Flight> getTodaysFlights(Location location) {
        Date today = truncate(new Date(), DAY_OF_MONTH);
        return flightRepository.findByLocationAndDate(location, today, addDays(today, 1));
    }

    private Flight findMatchingFlight(Flight ktraxFlight, List<Flight> flights) {
        Optional<Flight> matchingFlight = flights.stream()
                .filter(flight -> matches(flight, ktraxFlight))
                .findFirst();
        return matchingFlight.isPresent() ? matchingFlight.get() : null;
    }

    private boolean matches(Flight flight, Flight ktraxFlight) {
        if (sameAircraft(flight, ktraxFlight)) {
            if (flight.getStartTime() == null) {
                return true;
            }
            else {
                return truncate(flight.getStartTime(), MINUTE).equals(truncate(ktraxFlight.getStartTime(), MINUTE));
            }
        }
        return false;
    }

    private boolean sameAircraft(Flight flight, Flight ktraxFlight) {
        if (flight.getAircraft() != null && ktraxFlight.getAircraft() != null) {
            return StringUtils.equals(flight.getAircraft().getCallSign(), ktraxFlight.getAircraft().getCallSign());
        }
        else {
            return false;
        }
    }

    private boolean merge(Flight flight, Flight ktraxFlight) {
        boolean broadcast = false;
        flight.setSequence(ktraxFlight.getSequence());
        if (flight.getStartLocation() == null && ktraxFlight.getStartLocation() != null) {
            Location startingLocation = locationRepository.findByIcao(ktraxFlight.getStartLocation().getIcao());
            if (startingLocation != null) {
                flight.setStartLocation(startingLocation);
                broadcast = true;
            }
            else {
                LOG.warn("No start location found with name {}", ktraxFlight.getStartLocation().getIcao());
            }
        }
        if (flight.getStartTime() == null && ktraxFlight.getStartTime() != null) {
            flight.setStartTime(ktraxFlight.getStartTime());
            broadcast = true;
        }
        if (flight.getLandingLocation() == null && ktraxFlight.getLandingLocation() != null) {
            Location landingLocation = locationRepository.findByIcao(ktraxFlight.getLandingLocation().getIcao());
            if (landingLocation != null) {
                flight.setLandingLocation(landingLocation);
                broadcast = true;
            }
            else {
                LOG.warn("No location found with name {}", ktraxFlight.getLandingLocation().getIcao());
            }
        }
        if (flight.getLandingTime() == null && ktraxFlight.getLandingTime() != null) {
            flight.setLandingTime(ktraxFlight.getLandingTime());
            broadcast = true;
        }
        if (flight.getTowPlane() == null && ktraxFlight.getTowPlane() != null) {
            Aircraft towPlane = aircraftRepository.findAircraftByCallSign(ktraxFlight.getTowPlane().getCallSign());
            if (towPlane != null) {
                flight.setTowPlane(towPlane);
                broadcast = true;
            }
            else {
                LOG.warn("No tow plane found with call sign {}", ktraxFlight.getTowPlane().getCallSign());
            }
        }
        if (flight.getTowPlaneLandingTime() == null && ktraxFlight.getTowPlaneLandingTime() != null) {
            flight.setTowPlaneLandingTime(ktraxFlight.getTowPlaneLandingTime());
            broadcast = true;
        }
        if (broadcast) {
            flights.add(flightRepository.save(flight));
        }
        return broadcast;
    }

    @Handler
    public void handle(FlightDeleted flightDeleted) {
        long id = flightDeleted.getFlight().getId();
        flights
                .stream()
                .filter(flight -> flight.getId() == id)
                .findFirst().ifPresent(flight -> deletedSequences.add(flight.getSequence()));
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void clearFlightsAndDeletedSequences() {
        flights.clear();
        deletedSequences.clear();
        LOG.info("Deleted flights cleared");
    }

}
