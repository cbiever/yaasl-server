package yaasl.server.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spring.web.plugins.Docket;

import static springfox.documentation.builders.PathSelectors.regex;
import static springfox.documentation.spi.DocumentationType.SWAGGER_2;

@Profile("demo")
public class SwaggerConfig {

    @Bean
    public Docket aircraftApi() {
        return new Docket(SWAGGER_2)
                .groupName("Aircraft")
                .apiInfo(apiInfo())
                .select()
                .paths(regex("/rs/aircrafts/*.*"))
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

    @Bean
    public Docket locationsApi() {
        return new Docket(SWAGGER_2)
                .groupName("Locations")
                .apiInfo(apiInfo())
                .select()
                .paths(regex("/rs/locations/*.*"))
                .build()
                .pathMapping("https://localhost");
    }

    @Bean
    public Docket pilotsApi() {
        return new Docket(SWAGGER_2)
                .groupName("Pilots")
                .apiInfo(apiInfo())
                .select()
                .paths(regex("/rs/pilots/*.*"))
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
