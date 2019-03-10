package yaasl.server.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm.HS512
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices
import yaasl.server.model.User
import yaasl.server.persistence.TemporaryAuthorityRepository
import yaasl.server.persistence.UserRepository
import yaasl.server.security.SecurityConstants.EXPIRATION_TIME
import yaasl.server.security.SecurityConstants.TOKEN_PREFIX
import java.lang.Boolean.parseBoolean
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTAuthenticationFilter(authenticationManager: AuthenticationManager,
                              private val jwtSecret: String,
                              private val userRepository: UserRepository,
                              private val temporaryAuthorityRepository: TemporaryAuthorityRepository,
                              private val passwordEncoder: PasswordEncoder,
                              private val rememberMeServices: PersistentTokenBasedRememberMeServices) : UsernamePasswordAuthenticationFilter() {

    init {
        setAuthenticationManager(authenticationManager)
    }

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication? {
        var authentication = rememberMeServices.autoLogin(request, response)
        if (authentication == null) {
            authentication = super.attemptAuthentication(request, response)
        }
        if (authentication != null) {
            val user = userRepository.findByUsername(obtainUsername(request))!!
            if (user.MD5) {
                user.setPassword(passwordEncoder.encode(obtainPassword(request)))
                user.MD5 = false
                userRepository.save(user)
            }
            if (parseBoolean(request.getParameter("rememberMe"))) {
                rememberMeServices.loginSuccess(request, response, authentication)
            } else {
                rememberMeServices.logout(request, response, authentication)
            }
        }
        return authentication
    }

    override fun successfulAuthentication(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain, authentication: Authentication) {
        val roles = authentication.authorities.map { it.getAuthority() }.toMutableList()
        val temporaryAuthorities = temporaryAuthorityRepository.findByUserAndDate(authentication.principal as User, Date())
        roles.addAll(temporaryAuthorities.map { authority -> authority.authority }.toList())
        val token = Jwts.builder()
                .setSubject((authentication.principal as User).username)
                .setExpiration(Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .claim("roles", roles.toTypedArray())
                .signWith(HS512, jwtSecret.toByteArray())
                .compact()
        response.addHeader(AUTHORIZATION, TOKEN_PREFIX + token)
        response.addCookie(Cookie("yaasl", token).apply { isHttpOnly = true; secure = true; maxAge = (EXPIRATION_TIME / 1000).toInt() })
    }

}