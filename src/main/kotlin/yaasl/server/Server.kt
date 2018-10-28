package yaasl.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.scheduling.annotation.EnableScheduling
import springfox.documentation.swagger2.annotations.EnableSwagger2

@SpringBootApplication
@EnableScheduling
@ComponentScan("yaasl.server")
@EnableSwagger2
class Server

fun main(args: Array<String>) {
    runApplication<Server>(*args)
}

