package yaasl.server.demo

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.service.ApiInfo
import springfox.documentation.spring.web.plugins.Docket

import springfox.documentation.builders.PathSelectors.regex
import springfox.documentation.spi.DocumentationType.SWAGGER_2

@Profile("demo")
class SwaggerConfig {

    @Bean
    fun aircraftApi(): Docket {
        return Docket(SWAGGER_2)
                .groupName("Aircraft")
                .apiInfo(apiInfo())
                .select()
                .paths(regex("/rs/aircrafts/*.*"))
                .build()
    }

    @Bean
    fun flightsApi(): Docket {
        return Docket(SWAGGER_2)
                .groupName("Flights")
                .apiInfo(apiInfo())
                .select()
                .paths(regex("/rs/flights/*.*"))
                .build()
    }

    @Bean
    fun locationsApi(): Docket {
        return Docket(SWAGGER_2)
                .groupName("Locations")
                .apiInfo(apiInfo())
                .select()
                .paths(regex("/rs/locations/*.*"))
                .build()
                .pathMapping("https://localhost")
    }

    @Bean
    fun pilotsApi(): Docket {
        return Docket(SWAGGER_2)
                .groupName("Pilots")
                .apiInfo(apiInfo())
                .select()
                .paths(regex("/rs/pilots/*.*"))
                .build()
    }

    private fun apiInfo(): ApiInfo {
        return ApiInfoBuilder()
                .title("Yaasl (yet another automatic start list")
                .description("REST API of Yaasl")
                .version("1.0")
                .build()
    }

}
