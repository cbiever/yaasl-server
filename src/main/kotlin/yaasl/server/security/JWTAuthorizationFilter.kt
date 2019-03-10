package yaasl.server.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.socket.WebSocketHttpHeaders.SEC_WEBSOCKET_PROTOCOL
import yaasl.server.model.Authority
import yaasl.server.security.SecurityConstants.TOKEN_PREFIX
import java.util.regex.Pattern
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.streams.toList

class JWTAuthorizationFilter(private val jwtSecret: String) : OncePerRequestFilter() {

    private val LOG = LoggerFactory.getLogger(javaClass)
    private val jwtPattern = Pattern.compile("[\\w-]+\\.[\\w-]+\\.[\\w-]+")

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val authentication = getAuthentication(getToken(request, response))
        if (authentication != null) {
            SecurityContextHolder.getContext().authentication = authentication
        }
        chain.doFilter(request, response)
    }

    private fun getToken(request: HttpServletRequest, response: HttpServletResponse): String? {
        var token = getTokenFromWebsocketHeader(request)
        if (token != null) {
            response.addHeader(SEC_WEBSOCKET_PROTOCOL, "Yaasl")
        } else {
            token = getTokenFromCookie(request)
            if (token == null) {
                token = getTokenFromAuthorizationHeader(request)
            }
        }
        return token
    }

    private fun getTokenFromCookie(request: HttpServletRequest): String? = request.cookies?.filter { cookie -> cookie.name == "yaasl" }?.firstOrNull()?.value

    private fun getTokenFromAuthorizationHeader(request: HttpServletRequest): String? = request.getHeader(AUTHORIZATION)?.replace(TOKEN_PREFIX, "")

    private fun getTokenFromWebsocketHeader(request: HttpServletRequest): String? {
        val header = request.getHeader(SEC_WEBSOCKET_PROTOCOL)
        if (header != null && header.isNotEmpty()) {
            for (webSocketHeader in header.split(",")) {
                if (jwtPattern.matcher(webSocketHeader).matches()) {
                    return webSocketHeader.replace(TOKEN_PREFIX, "")
                }
            }
        }
        return null
    }

    private fun getAuthentication(token: String?): UsernamePasswordAuthenticationToken? {
        var authenticationToken: UsernamePasswordAuthenticationToken? = null
        try {
            if (token != null) {
                var claims: Claims? = null
                try {
                    claims = Jwts.parser()
                            .setSigningKey(jwtSecret.toByteArray())
                            .parseClaimsJws(token)
                            .getBody()
                } catch (e: ExpiredJwtException) {
                    LOG.error("Token expired")
                }

                if (claims != null) {
                    val user = claims.subject
                    if (user != null) {
                        val roles = claims["roles"] as List<String>
                        var grantedAuthorities = roles.stream().map { role -> Authority().apply { authority = role } }.toList()
                        authenticationToken = UsernamePasswordAuthenticationToken(user, null, grantedAuthorities)
                    }
                }
            }
        } catch (se: SignatureException) {
            LOG.error("Invalid JWT token")
        }
        return authenticationToken
    }

}
