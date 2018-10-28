package yaasl.server.event

import net.engio.mbassy.bus.MBassador
import net.engio.mbassy.bus.config.BusConfiguration
import net.engio.mbassy.bus.config.Feature
import net.engio.mbassy.bus.error.PublicationError
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class EventBusConfig {

    private val LOG = LoggerFactory.getLogger(javaClass)

    @Bean
    fun getMbassador(): MBassador<Any> {
        val busConfiguration = BusConfiguration()
                .addFeature(Feature.SyncPubSub.Default())
                .addFeature(Feature.AsynchronousHandlerInvocation.Default())
                .addFeature(Feature.AsynchronousMessageDispatch.Default())
                .addPublicationErrorHandler { error: PublicationError -> LOG.error("Error during publish/subscribe dispatching", error.cause) }
        return MBassador(busConfiguration)
    }

}
