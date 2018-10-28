package yaasl.server.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm.HS512
import org.apache.commons.lang3.StringUtils.isNotEmpty
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices
import org.springframework.stereotype.Component
import yaasl.server.model.User
import yaasl.server.persistence.TemporaryAuthorityRepository
import yaasl.server.persistence.UserRepository
import yaasl.server.security.SecurityConstants.EXPIRATION_TIME
import yaasl.server.security.SecurityConstants.TOKEN_PREFIX
import java.lang.Boolean.parseBoolean
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTAuthenticationFilter(private val jwtSecret: String,
                              private val authenticationManager: AuthenticationManager,
                              private val userRepository: UserRepository,
                              private val temporaryAuthorityRepository: TemporaryAuthorityRepository,
                              private val passwordEncoder: PasswordEncoder,
                              private val rememberMeServices: PersistentTokenBasedRememberMeServices) : UsernamePasswordAuthenticationFilter() {

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse?): Authentication? {
        var authentication: Authentication? = rememberMeServices.autoLogin(request, response)
        if (authentication == null) {
            val username = request.getParameter(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY)
            val password = request.getParameter(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_PASSWORD_KEY)
            if (isNotEmpty(username) && isNotEmpty(password)) {
                var token = UsernamePasswordAuthenticationToken(username, password)
                authentication = authenticationManager.authenticate(token)
                if (authentication != null) {
                    val user = userRepository.findByUsername(username)!!
                    if (user.MD5) {
                        user.setPassword(passwordEncoder.encode(password))
                        user.MD5 = false
                        userRepository.save(user)
                        token = UsernamePasswordAuthenticationToken(username, password)
                        authentication = authenticationManager.authenticate(token)
                    }
                    if (parseBoolean(request.getParameter("rememberMe"))) {
                        rememberMeServices.loginSuccess(request, response, authentication)
                    } else {
                        rememberMeServices.logout(request, response, authentication)
                    }
                }
            }
        }
        return authentication
    }

    override fun successfulAuthentication(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain?, authentication: Authentication) {
        val roles = authentication
                .authorities
                .map { it.getAuthority() }
                .toMutableList()
        val temporaryAuthorities = temporaryAuthorityRepository.findByUserAndDate(authentication.principal as User, Date())
        roles.addAll(temporaryAuthorities.map { authority -> authority.authority }.toList())
        val token = Jwts.builder()
                .setSubject((authentication.principal as User).username)
                .setExpiration(Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .claim("roles", roles.toTypedArray())
                .signWith(HS512, jwtSecret.toByteArray())
                .compact()
        response.addHeader(AUTHORIZATION, TOKEN_PREFIX + token)
    }

    override fun getAuthenticationManager(): AuthenticationManager {
        return authenticationManager
    }

}