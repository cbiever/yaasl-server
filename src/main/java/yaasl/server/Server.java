package yaasl.server;

import static springfox.documentation.builders.PathSelectors.regex;
import static springfox.documentation.spi.DocumentationType.SWAGGER_2;
import static yaasl.server.convert.Converter.addDays;
import static yaasl.server.convert.Converter.addMinutes;
import static yaasl.server.model.PilotRole.PILOT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import yaasl.server.controller.AircraftController;
import yaasl.server.jsonapi.MultiData;
import yaasl.server.model.*;
import yaasl.server.persistence.AircraftRepository;
import yaasl.server.persistence.FlightsRepository;
import yaasl.server.persistence.LocationRepository;
import yaasl.server.persistence.PilotsRepository;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Date;

@SpringBootApplication
@EnableScheduling
@ComponentScan("yaasl.server")
@EnableSwagger2
public class Server {

    public static void main(String[] args) {
        SpringApplication.run(Server.class, args);
    }

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private PilotsRepository pilotsRepository;

    @Autowired
    private AircraftRepository aircraftRepository;

    @Autowired
    private FlightsRepository flightsRepository;

    @PostConstruct
    public void init() {
        if (locationRepository.count() == 0) {
            locationRepository.save(new Location("LSZB"));
            locationRepository.save(new Location("LSZW"));
            locationRepository.save(new Location("LSTB"));

            aircraftRepository.save(new Aircraft("HB-1766", false, 2));
            aircraftRepository.save(new Aircraft("HB-1811", false, 2));
            aircraftRepository.save(new Aircraft("HB-3131", false, 2));
            aircraftRepository.save(new Aircraft("HB-3362", false, 2));
            aircraftRepository.save(new Aircraft("HB-3411", false, 2));
            aircraftRepository.save(new Aircraft("HB-3022", false, 1));
            aircraftRepository.save(new Aircraft("HB-3043", false, 1));
            aircraftRepository.save(new Aircraft("HB-3299", false, 1));
            aircraftRepository.save(new Aircraft("HB-3447", false, 1));
            aircraftRepository.save(new Aircraft("HB-3453", false, 1));

            aircraftRepository.save(new Aircraft("HB-HHO", true, 4));
            aircraftRepository.save(new Aircraft("HB-2377", true, 2));

            pilotsRepository.save(new Pilot("Han Solo"));
            pilotsRepository.save(new Pilot("Chewbacca"));
            pilotsRepository.save(new Pilot("Luke Skywalker"));
            pilotsRepository.save(new Pilot("Jabba the Hutt"));

            pilotsRepository.save(new Pilot("Black Mamba"));
            pilotsRepository.save(new Pilot("Bill"));
            pilotsRepository.save(new Pilot("Hattori Hanzo"));
            pilotsRepository.save(new Pilot("O-Ren Ishii"));
            pilotsRepository.save(new Pilot("Gogo Yubari"));

            pilotsRepository.save(new Pilot("Keyser Soze"));
            pilotsRepository.save(new Pilot("McManus"));
            pilotsRepository.save(new Pilot("Dean Keaton"));
            pilotsRepository.save(new Pilot("Fred Fenster"));
            pilotsRepository.save(new Pilot("Todd Hockney"));
            pilotsRepository.save(new Pilot("Verbal Kint"));
            pilotsRepository.save(new Pilot("Dave Kujan"));
            pilotsRepository.save(new Pilot("Edie Finneran"));
            pilotsRepository.save(new Pilot("Mr. Kobayashi"));

            Date now = new Date();

            Flight flight = new Flight();
            flight.setLocation(locationRepository.findByName("LSZW"));
            flight.setAircraft(aircraftRepository.findOne(1L));
            flight.setPilot1(pilotsRepository.findOne(1L));
            flight.setPilot2(pilotsRepository.findOne(2L));
            flight.setStartTime(addDays(now, -1));
            flight.setLandingTime(addMinutes(addDays(now, -1), 5));
            flightsRepository.save(flight);

            flight = new Flight();
            flight.setLocation(locationRepository.findByName("LSZW"));
            flight.setAircraft(aircraftRepository.findOne(2L));
            flight.setPilot1(pilotsRepository.findOne(3L));
            flight.setPilot2(pilotsRepository.findOne(4L));
            flight.setStartTime(now);
            flight.setLandingTime(addMinutes(now, 5));
            flightsRepository.save(flight);

            flight = new Flight();
            flight.setLocation(locationRepository.findByName("LSZB"));
            flight.setAircraft(aircraftRepository.findOne(3L));
            flight.setPilot1(pilotsRepository.findOne(5L));
            flight.setPilot2(pilotsRepository.findOne(6L));
            flight.setStartTime(addMinutes(addDays(now, -1), 5));
            flight.setLandingTime(now);
            flightsRepository.save(flight);

            flight = new Flight();
            flight.setLocation(locationRepository.findByName("LSZB"));
            flight.setAircraft(aircraftRepository.findOne(4L));
            flight.setPilot1(pilotsRepository.findOne(7L));
            flight.setPilot2(pilotsRepository.findOne(8L));
            flight.setStartTime(now);
            flight.setLandingTime(addMinutes(now, 5));
            flightsRepository.save(flight);

            flight = new Flight();
            flight.setLocation(locationRepository.findByName("LSTB"));
            flight.setAircraft(aircraftRepository.findOne(5L));
            flight.setPilot1(pilotsRepository.findOne(7L));
            flight.setPilot2(pilotsRepository.findOne(8L));
            flight.setStartTime(addMinutes(addDays(now, -1), 5));
            flight.setLandingTime(now);
            flightsRepository.save(flight);

            flight = new Flight();
            flight.setLocation(locationRepository.findByName("LSTB"));
            flight.setAircraft(aircraftRepository.findOne(6L));
            flight.setPilot1(pilotsRepository.findOne(9L));
            flight.setStartTime(now);
            flight.setLandingTime(addMinutes(now, 5));
            flightsRepository.save(flight);
        }
    }

    @Bean
    public Docket locationsApi() {
        return new Docket(SWAGGER_2)
                .groupName("Locations")
                .apiInfo(apiInfo())
                .select()
                .paths(regex("/rs/locations/*.*"))
                .build();
    }

    @Bean
    public Docket aircraftApi() {
        return new Docket(SWAGGER_2)
                .groupName("Aircraft")
                .apiInfo(apiInfo())
                .select()
                .paths(regex("/rs/aircrafts"))
                .build();
    }

    @Bean
    public Docket pilotsApi() {
        return new Docket(SWAGGER_2)
                .groupName("Pilots")
                .apiInfo(apiInfo())
                .select()
                .paths(regex("/rs/pilots"))
                .build();
    }

    @Bean
    public Docket flightsApi() {
        return new Docket(SWAGGER_2)
                .groupName("Flights")
                .apiInfo(apiInfo())
                .select()
                .paths(regex("/rs/flights/*.*"))
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Yaasl (yet another automatic start list")
                .description("REST API of Yaasl")
                .version("1.0")
                .build();
    }

}
