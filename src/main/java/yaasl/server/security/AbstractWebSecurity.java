package yaasl.server.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import javax.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.random;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static yaasl.server.security.SecurityConstants.SIGN_UP_URL;

public abstract class AbstractWebSecurity extends WebSecurityConfigurerAdapter {

    private Logger LOG = LoggerFactory.getLogger(getClass());

    @Autowired
    protected UserService userService;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected TemporaryAuthorityRepository temporaryAuthorityRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Value("${security.jwt.secret:''}")
    protected String jwtSecret;

    @Value("${security.remember.me.key:''}")
    protected String rememberMeKey;

    @PostConstruct
    public void init() {
        if (isEmpty(jwtSecret)) {
            this.jwtSecret = random(10, true, true);
            LOG.info("JWT secret generated");
        }
        if (isEmpty(rememberMeKey)) {
            this.rememberMeKey = random(10, true, true);
            LOG.info("Remember me key generated");
        }
    }

    protected abstract String[] getPermitAllUrls();

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                .disable()

                .csrf()
                .disable()

                .headers().frameOptions().sameOrigin()

                .and()

                .authorizeRequests()
                .antMatchers(POST, SIGN_UP_URL)
                .permitAll()
                .antMatchers(getPermitAllUrls())
                .permitAll()
                .anyRequest()
                .authenticated()

                .and()

                .addFilter(new JWTAuthenticationFilter(
                        jwtSecret,
                        authenticationManager(),
                        userRepository,
                        temporaryAuthorityRepository,
                        passwordEncoder,
                        new RememberMeService(rememberMeKey, userService)))
                .addFilter(new JWTAuthorizationFilter(jwtSecret, authenticationManager()))

                .sessionManagement()
                .sessionCreationPolicy(STATELESS)

                .and()

                .servletApi()
                .rolePrefix("");
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(userService)
                .passwordEncoder(passwordEncoder);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }

}
