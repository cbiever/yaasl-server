package yaasl.server.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import yaasl.server.persistence.TemporaryAuthorityRepository;
import yaasl.server.persistence.UserRepository;
import yaasl.server.security.*;

import java.util.List;

import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static yaasl.server.security.SecurityConstants.SIGN_UP_URL;

@EnableWebSecurity
@Profile("demo")
public class WebSecurity extends AbstractWebSecurity {

    @Override
    protected String[] getPermitAllUrls() {
        return new String[] { "/h2-console/**", "/swagger-ui.html/**", "/swagger-resources/**", "/v2/api-docs/**", "/webjars/**" };
    }

}
