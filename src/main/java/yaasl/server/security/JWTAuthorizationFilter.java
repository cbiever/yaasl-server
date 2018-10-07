package yaasl.server.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import yaasl.server.model.Authority;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.web.socket.WebSocketHttpHeaders.SEC_WEBSOCKET_PROTOCOL;
import static yaasl.server.security.SecurityConstants.TOKEN_PREFIX;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private Logger LOG = LoggerFactory.getLogger(getClass());
    private Pattern jwtPattern = Pattern.compile("[\\w-]+\\.[\\w-]+\\.[\\w-]+");
    private String jwtSecret;

    public JWTAuthorizationFilter(String jwtSecret, AuthenticationManager authenticationManager) {
        super(authenticationManager);
        this.jwtSecret = jwtSecret;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        UsernamePasswordAuthenticationToken authentication = getAuthentication(getToken(request, response));
        if (authentication != null) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }

    private String getToken(HttpServletRequest request, HttpServletResponse response) {
        String header = request.getHeader(AUTHORIZATION);
        if (header == null) {
            header = request.getHeader(SEC_WEBSOCKET_PROTOCOL);
            if (isNotEmpty(header)) {
                for (String webSocketHeader : header.split(",")) {
                    if (jwtPattern.matcher(webSocketHeader).matches()) {
                        header = webSocketHeader;
                    }
                    else {
                        response.addHeader(SEC_WEBSOCKET_PROTOCOL, webSocketHeader);
                    }
                }
            }
        }
        return header != null ? header.replace(TOKEN_PREFIX, "") : null;
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String token) {
        UsernamePasswordAuthenticationToken authenticationToken = null;
        try {
            if (token != null) {
                Claims claims = null;
                try {
                    claims = Jwts.parser()
                            .setSigningKey(jwtSecret.getBytes())
                            .parseClaimsJws(token)
                            .getBody();
                }
                catch (ExpiredJwtException e) {
                    LOG.error("Token expired");
                }
                if (claims != null) {
                    String user = claims.getSubject();
                    if (user != null) {
                        List<GrantedAuthority> grantedAuthorities = null;
                        List<String> roles = (List<String>) claims.get("roles");
                        if (roles != null) {
                            grantedAuthorities = roles.stream().map(role -> new Authority(role)).collect(toList());
                        }
                        authenticationToken = new UsernamePasswordAuthenticationToken(user, null, grantedAuthorities);
                    }
                }
            }
        }
        catch (SignatureException se) {
            LOG.error("Invalid JWT token");
        }
        return authenticationToken;
    }

}
