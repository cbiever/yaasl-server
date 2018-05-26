package yaasl.server.event;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.config.BusConfiguration;
import net.engio.mbassy.bus.config.Feature;
import net.engio.mbassy.bus.config.IBusConfiguration;
import net.engio.mbassy.bus.error.PublicationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventBusConfig {

    private Logger LOG = LoggerFactory.getLogger(getClass());

    private MBassador mBassador;

    @Bean
    public MBassador getMbassador() {
        if (mBassador == null) {
            IBusConfiguration busConfiguration = new BusConfiguration()
                    .addFeature(Feature.SyncPubSub.Default())
                    .addFeature(Feature.AsynchronousHandlerInvocation.Default())
                    .addFeature(Feature.AsynchronousMessageDispatch.Default())
                    .addPublicationErrorHandler((PublicationError error) -> {
                        LOG.error("Error during publish/subscribe dispatching", error.getCause());
                    });
            mBassador = new MBassador(busConfiguration);
        }
        return mBassador;
    }

}
