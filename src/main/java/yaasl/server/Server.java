package yaasl.server;

import static springfox.documentation.builders.PathSelectors.regex;
import static springfox.documentation.spi.DocumentationType.SWAGGER_2;
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
import yaasl.server.model.Aircraft;
import yaasl.server.model.Flight;
import yaasl.server.model.Pilot;
import yaasl.server.persistence.AircraftRepository;
import yaasl.server.persistence.FlightsRepository;
import yaasl.server.persistence.PilotsRepository;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.OffsetDateTime;

@SpringBootApplication
@EnableScheduling
@ComponentScan("yaasl.server")
@EnableSwagger2
public class Server {

    public static void main(String[] args) {
        SpringApplication.run(Server.class, args);
    }

    @Autowired
    private PilotsRepository pilotsRepository;

    @Autowired
    private AircraftRepository aircraftRepository;

    @Autowired
    private FlightsRepository flightsRepository;

    @PostConstruct
    public void init() {
        aircraftRepository.save(new Aircraft("HB-1766", 2));
        aircraftRepository.save(new Aircraft("HB-1811", 2));
        aircraftRepository.save(new Aircraft("HB-3131", 2));
        aircraftRepository.save(new Aircraft("HB-3362", 2));
        aircraftRepository.save(new Aircraft("HB-3411", 2));
        aircraftRepository.save(new Aircraft("HB-3022", 1));
        aircraftRepository.save(new Aircraft("HB-3043", 1));
        aircraftRepository.save(new Aircraft("HB-3299", 1));
        aircraftRepository.save(new Aircraft("HB-3447", 1));
        aircraftRepository.save(new Aircraft("HB-3453", 1));

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

        Flight flight = new Flight();
        flight.setAircraft(aircraftRepository.findOne(1L));
        flight.setPilot1(pilotsRepository.findOne(1L));
        flight.setPilot2(pilotsRepository.findOne(2L));
        flight.setStartTime(OffsetDateTime.now());
        flight.setLandingTime(OffsetDateTime.now());
        flightsRepository.save(flight);
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

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Yaasl (yet another automatic start list")
                .description("REST API of Yaasl")
                .version("1.0")
                .build();
    }

}
