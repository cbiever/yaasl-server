package yaasl.server.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import yaasl.server.persistence.UserRepository;
import yaasl.server.security.*;

import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static yaasl.server.security.SecurityConstants.SIGN_UP_URL;

@EnableWebSecurity
@Profile("demo")
public class WebSecurity extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()

                .and()

                .csrf()
                .disable()

                .headers().frameOptions().sameOrigin()

                .and()

                .authorizeRequests()
                .antMatchers(POST, SIGN_UP_URL)
                .permitAll()
                .antMatchers("/h2-console/**", "/swagger-ui.html/**", "/swagger-resources/**", "/v2/api-docs/**", "/webjars/**")
                .permitAll()
                .anyRequest()
                .authenticated()

                .and()

                .addFilter(new JWTAuthenticationFilter(authenticationManager(), userRepository, passwordEncoder, new RememberMeService("test", userService)))
                .addFilter(new JWTAuthorizationFilter(authenticationManager()))

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
