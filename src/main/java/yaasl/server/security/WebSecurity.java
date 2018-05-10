package yaasl.server.security;

import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableWebSecurity
@Profile("prod")
public class WebSecurity extends AbstractWebSecurity {

    @Override
    protected String[] getPermitAllUrls() {
        return new String[0];
    }

}
