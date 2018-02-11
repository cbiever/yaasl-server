package yaasl.server.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import yaasl.server.model.*;
import yaasl.server.persistence.*;

import javax.annotation.PostConstruct;
import java.util.Date;

import static org.apache.commons.lang3.time.DateUtils.addDays;
import static org.apache.commons.lang3.time.DateUtils.addMinutes;

@Configuration
public class DatabaseConfig {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private PilotRoleRepository pilotRoleRepository;

    @Autowired
    private PilotsRepository pilotsRepository;

    @Autowired
    private AircraftRepository aircraftRepository;

    @Autowired
    private FlightsRepository flightsRepository;

    @Autowired
    private CostSharingRepository costSharingRepository;

    @PostConstruct
    public void initDatabase() {
        if (locationRepository.count() == 0) {
            locationRepository.save(new Location("LSZB"));
            locationRepository.save(new Location("LSZW"));
            locationRepository.save(new Location("LSTB"));

            aircraftRepository.save(new Aircraft("HB-1766", true, false, 2));
            aircraftRepository.save(new Aircraft("HB-1811", true, false, 2));
            aircraftRepository.save(new Aircraft("HB-3131", true, false, 2));
            aircraftRepository.save(new Aircraft("HB-3362", true, false, 2));
            aircraftRepository.save(new Aircraft("HB-3411", true, false, 2));
            aircraftRepository.save(new Aircraft("HB-3022", true, false, 1));
            aircraftRepository.save(new Aircraft("HB-3043", true, false, 1));
            aircraftRepository.save(new Aircraft("HB-3299", true, false, 1));
            aircraftRepository.save(new Aircraft("HB-3447", true, false, 1));
            aircraftRepository.save(new Aircraft("HB-3453", true, false, 1));

            aircraftRepository.save(new Aircraft("HB-HHO",  false, true, 4));
            aircraftRepository.save(new Aircraft("HB-2377", false, true, 2));

            pilotRoleRepository.save(new PilotRole("Flight instructor", "pilot.role.fi"));
            pilotRoleRepository.save(new PilotRole("Pilot", "pilot.role.pilot"));
            pilotRoleRepository.save(new PilotRole("Student", "pilot.role.student"));
            pilotRoleRepository.save(new PilotRole("Passenger", "pilot.role.passenger"));

            pilotsRepository.save(new Pilot("Han Solo", pilotRoleRepository.findByDescription("Flight instructor"), false));
            pilotsRepository.save(new Pilot("Chewbacca", pilotRoleRepository.findByDescription("Student"), false));
            pilotsRepository.save(new Pilot("Luke Skywalker", pilotRoleRepository.findByDescription("Pilot"), false));
            pilotsRepository.save(new Pilot("Jabba the Hutt", pilotRoleRepository.findByDescription("Student"), true));

            pilotsRepository.save(new Pilot("Black Mamba", pilotRoleRepository.findByDescription("Flight instructor"), false));
            pilotsRepository.save(new Pilot("Bill", pilotRoleRepository.findByDescription("Flight instructor"), false));
            pilotsRepository.save(new Pilot("Hattori Hanzo", pilotRoleRepository.findByDescription("Flight instructor"), true));
            pilotsRepository.save(new Pilot("O-Ren Ishii", pilotRoleRepository.findByDescription("Student"), false));
            pilotsRepository.save(new Pilot("Gogo Yubari", pilotRoleRepository.findByDescription("Passenger"), false));

            pilotsRepository.save(new Pilot("Keyser Soze", pilotRoleRepository.findByDescription("Flight instructor"), true));
            pilotsRepository.save(new Pilot("McManus", pilotRoleRepository.findByDescription("Student"), false));
            pilotsRepository.save(new Pilot("Dean Keaton", pilotRoleRepository.findByDescription("Student"), false));
            pilotsRepository.save(new Pilot("Fred Fenster", pilotRoleRepository.findByDescription("Pilot"), false));
            pilotsRepository.save(new Pilot("Todd Hockney", pilotRoleRepository.findByDescription("Pilot"), false));
            pilotsRepository.save(new Pilot("Verbal Kint", pilotRoleRepository.findByDescription("Flight instructor"), false));
            pilotsRepository.save(new Pilot("Dave Kujan", pilotRoleRepository.findByDescription("Passenger"), false));
            pilotsRepository.save(new Pilot("Edie Finneran", pilotRoleRepository.findByDescription("Student"), false));
            pilotsRepository.save(new Pilot("Mr. Kobayashi", pilotRoleRepository.findByDescription("Passenger"), true));

            Date now = new Date();

            Flight flight = new Flight();
            flight.setStartLocation(locationRepository.findByName("LSZW"));
            flight.setLandingLocation(locationRepository.findByName("LSZW"));
            flight.setAircraft(aircraftRepository.findOne(1L));
            flight.setPilot1(pilotsRepository.findOne(1L));
            flight.setPilot2(pilotsRepository.findOne(2L));
            flight.setStartTime(addDays(now, -1));
            flight.setLandingTime(addMinutes(addDays(now, -1), 5));
            flightsRepository.save(flight);

            flight = new Flight();
            flight.setStartLocation(locationRepository.findByName("LSZW"));
            flight.setLandingLocation(locationRepository.findByName("LSZW"));
            flight.setAircraft(aircraftRepository.findOne(2L));
            flight.setPilot1(pilotsRepository.findOne(3L));
            flight.setPilot2(pilotsRepository.findOne(4L));
            flight.setStartTime(now);
            flight.setLandingTime(addMinutes(now, 5));
            flightsRepository.save(flight);

            flight = new Flight();
            flight.setStartLocation(locationRepository.findByName("LSZB"));
            flight.setLandingLocation(locationRepository.findByName("LSZB"));
            flight.setAircraft(aircraftRepository.findOne(3L));
            flight.setPilot1(pilotsRepository.findOne(5L));
            flight.setPilot2(pilotsRepository.findOne(6L));
            flight.setStartTime(addMinutes(addDays(now, -1), 5));
            flight.setLandingTime(now);
            flightsRepository.save(flight);

            flight = new Flight();
            flight.setStartLocation(locationRepository.findByName("LSZB"));
            flight.setLandingLocation(locationRepository.findByName("LSZB"));
            flight.setAircraft(aircraftRepository.findOne(4L));
            flight.setPilot1(pilotsRepository.findOne(7L));
            flight.setPilot2(pilotsRepository.findOne(8L));
            flight.setStartTime(now);
            flight.setLandingTime(addMinutes(now, 5));
            flightsRepository.save(flight);

            flight = new Flight();
            flight.setStartLocation(locationRepository.findByName("LSTB"));
            flight.setLandingLocation(locationRepository.findByName("LSTB"));
            flight.setAircraft(aircraftRepository.findOne(5L));
            flight.setPilot1(pilotsRepository.findOne(7L));
            flight.setPilot2(pilotsRepository.findOne(8L));
            flight.setStartTime(addMinutes(addDays(now, -1), 5));
            flight.setLandingTime(now);
            flightsRepository.save(flight);

            flight = new Flight();
            flight.setStartLocation(locationRepository.findByName("LSTB"));
            flight.setLandingLocation(locationRepository.findByName("LSTB"));
            flight.setAircraft(aircraftRepository.findOne(6L));
            flight.setPilot1(pilotsRepository.findOne(9L));
            flight.setStartTime(now);
            flight.setLandingTime(addMinutes(now, 5));
            flightsRepository.save(flight);

            costSharingRepository.save(new CostSharing("Student", "cost.sharing.student"));
            costSharingRepository.save(new CostSharing("FiftyFifty", "cost.sharing.fifty.fifty"));
        }

    }

}
