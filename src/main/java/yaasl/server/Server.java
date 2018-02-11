package yaasl.server;

import static org.apache.commons.lang3.time.DateUtils.addDays;
import static org.apache.commons.lang3.time.DateUtils.addMinutes;
import static springfox.documentation.builders.PathSelectors.regex;
import static springfox.documentation.spi.DocumentationType.SWAGGER_2;

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
import yaasl.server.persistence.*;

import javax.annotation.PostConstruct;
import java.util.Date;

@SpringBootApplication
@EnableScheduling
@ComponentScan("yaasl.server")
@EnableSwagger2
public class Server {

    public static void main(String[] args) {
        SpringApplication.run(Server.class, args);
    }

}
