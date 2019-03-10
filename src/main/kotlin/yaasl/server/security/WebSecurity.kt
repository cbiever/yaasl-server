package yaasl.server.security

import org.apache.commons.lang3.RandomStringUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.core.env.Environment
import org.springframework.core.env.Profiles
import org.springframework.http.HttpMethod.POST
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy.STATELESS
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import yaasl.server.persistence.TemporaryAuthorityRepository
import yaasl.server.persistence.UserRepository
import yaasl.server.security.SecurityConstants.REMEMBER_ME_COOKIE
import yaasl.server.security.SecurityConstants.REMEMBER_ME_EXPIRATION_TIME
import yaasl.server.security.SecurityConstants.SIGN_UP_URL

@EnableWebSecurity
class WebSecurity(@Value("\${security.jwt.secret:''}") private var jwtSecret: String,
                  @Value("\${security.remember.me.key:''}") private var rememberMeKey: String,
                  private val yaaslUserDetailsService: YaaslUserDetailsService,
                  private val passwordEncoder: PasswordEncoder,
                  private val userRepository: UserRepository,
                  private val temporaryAuthorityRepository: TemporaryAuthorityRepository,
                  private val environment: Environment) : WebSecurityConfigurerAdapter() {

    private val LOG = LoggerFactory.getLogger(javaClass)

    init {
        if (jwtSecret.isEmpty()) {
            jwtSecret = RandomStringUtils.random(10, true, true)
            LOG.info("JWT secret generated")
        }
        if (rememberMeKey.isEmpty()) {
            this.rememberMeKey = RandomStringUtils.random(10, true, true)
            LOG.info("Remember me key generated")
        }
    }

    override fun configure(http: HttpSecurity) {
        http
                .cors()
                .disable()

                .csrf()
                .disable()

                .headers()
                .frameOptions()
                .sameOrigin()

                .and()

                .authorizeRequests()
                .antMatchers(POST, SIGN_UP_URL)
                .permitAll()
                .antMatchers(*permitAllUrls())
                .permitAll()
                .anyRequest()
                .authenticated()

                .and()
                .addFilter(jwtAuthenticationFilter())
                .addFilterAfter(jwtAuthorizationFilter(), BasicAuthenticationFilter::class.java)

                .sessionManagement()
                .sessionCreationPolicy(STATELESS)

                .and()

                .servletApi()
                .rolePrefix("")
    }

    public override fun configure(authenticationManagerBuilder: AuthenticationManagerBuilder) {
        authenticationManagerBuilder
                .userDetailsService(yaaslUserDetailsService)
                .passwordEncoder(passwordEncoder)
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", CorsConfiguration().applyPermitDefaultValues())
        return source
    }

    fun permitAllUrls(): Array<String> {
        if (environment.acceptsProfiles(Profiles.of("demo"))) {
            return arrayOf("/h2-console/**", "/swagger-ui.html/**", "/swagger-resources/**", "/v2/api-docs/**", "/webjars/**")
        } else {
            return emptyArray()
        }
    }

    private fun jwtAuthenticationFilter() = JWTAuthenticationFilter(
            authenticationManager(),
            jwtSecret,
            userRepository,
            temporaryAuthorityRepository,
            passwordEncoder,
            rememberMeService())

    private fun jwtAuthorizationFilter() = JWTAuthorizationFilter(jwtSecret)

    private fun rememberMeService() = PersistentTokenBasedRememberMeServices(rememberMeKey, yaaslUserDetailsService, yaaslUserDetailsService).apply {
        parameter = "rememberMe";
        setCookieName(REMEMBER_ME_COOKIE);
        setTokenValiditySeconds(REMEMBER_ME_EXPIRATION_TIME)
        setUseSecureCookie(true)
    }

}
