package yaasl.server.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import yaasl.server.model.*;
import yaasl.server.persistence.*;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.Optional;

import static org.apache.commons.lang3.time.DateUtils.addDays;
import static org.apache.commons.lang3.time.DateUtils.addMinutes;

@Configuration
@Profile("demo")
public class DatabaseConfig {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private PilotRepository pilotRepository;

    @Autowired
    private AircraftRepository aircraftRepository;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private TemporaryAuthorityRepository temporaryAuthorityRepository;

    @PostConstruct
    public void initDatabase() {
        Date now = new Date();

        Flight flight = new Flight();
        flight.setStartLocation(locationRepository.findByIcao("LSZW"));
        flight.setLandingLocation(locationRepository.findByIcao("LSZW"));
        flight.setAircraft(getAircraft(1L));
        flight.setPilot1(getPilot(1L));
        flight.setPilot2(getPilot(2L));
        flight.setStartTime(addDays(now, -1));
        flight.setLandingTime(addMinutes(addDays(now, -1), 5));
        flight.setEditable(false);
        flight.setLocked(true);
        flightRepository.save(flight);

        flight = new Flight();
        flight.setStartLocation(locationRepository.findByIcao("LSZW"));
        flight.setLandingLocation(locationRepository.findByIcao("LSZW"));
        flight.setAircraft(getAircraft(2L));
        flight.setPilot1(getPilot(3L));
        flight.setPilot2(getPilot(4L));
        flight.setStartTime(now);
        flight.setLandingTime(addMinutes(now, 5));
        flight.setEditable(true);
        flight.setLocked(false);
        flightRepository.save(flight);

        flight = new Flight();
        flight.setStartLocation(locationRepository.findByIcao("LSZB"));
        flight.setLandingLocation(locationRepository.findByIcao("LSZB"));
        flight.setAircraft(getAircraft(3L));
        flight.setPilot1(getPilot(5L));
        flight.setPilot2(getPilot(6L));
        flight.setStartTime(addMinutes(addDays(now, -1), 5));
        flight.setLandingTime(addMinutes(addDays(now, -1), 25));
        flight.setEditable(false);
        flight.setLocked(true);
        flightRepository.save(flight);

        flight = new Flight();
        flight.setStartLocation(locationRepository.findByIcao("LSZB"));
        flight.setLandingLocation(locationRepository.findByIcao("LSZB"));
        flight.setAircraft(getAircraft(4L));
        flight.setPilot1(getPilot(7L));
        flight.setPilot2(getPilot(8L));
        flight.setStartTime(now);
        flight.setLandingTime(addMinutes(now, 5));
        flight.setEditable(true);
        flight.setLocked(false);
        flightRepository.save(flight);

        flight = new Flight();
        flight.setStartLocation(locationRepository.findByIcao("LSTB"));
        flight.setLandingLocation(locationRepository.findByIcao("LSTB"));
        flight.setAircraft(getAircraft(5L));
        flight.setPilot1(getPilot(7L));
        flight.setPilot2(getPilot(8L));
        flight.setStartTime(addMinutes(addDays(now, -1), 5));
        flight.setLandingTime(addMinutes(addDays(now, -1), 45));
        flight.setEditable(false);
        flight.setLocked(true);
        flightRepository.save(flight);

        flight = new Flight();
        flight.setStartLocation(locationRepository.findByIcao("LSTB"));
        flight.setLandingLocation(locationRepository.findByIcao("LSTB"));
        flight.setAircraft(getAircraft(6L));
        flight.setPilot1(getPilot(9L));
        flight.setStartTime(now);
        flight.setLandingTime(addMinutes(now, 5));
        flight.setEditable(true);
        flight.setLocked(false);
        flightRepository.save(flight);

        User fdl = userRepository.findByUsername("fdl");
        Authority authority = authorityRepository.findByName("fdl");
        TemporaryAuthority temporaryFdlAuthority = new TemporaryAuthority(now, fdl, authority);
        temporaryAuthorityRepository.save(temporaryFdlAuthority);
    }

    private Aircraft getAircraft(long id) {
        Optional<Aircraft> aircraft = aircraftRepository.findById(id);
        return aircraft.isPresent() ? aircraft.get() : null;
    }

    private Pilot getPilot(long id) {
        Optional<Pilot> pilot = pilotRepository.findById(id);
        return pilot.isPresent() ? pilot.get() : null;
    }

}
