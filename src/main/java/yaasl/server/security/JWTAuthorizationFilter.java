package yaasl.server.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
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
import java.util.Set;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;
import static yaasl.server.security.SecurityConstants.*;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private Pattern jwtPattern = Pattern.compile("[\\w-]+\\.[\\w-]+\\.[\\w-]+");

    public JWTAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
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
        String header = request.getHeader(TOKEN_HEADER);
        if (header == null) {
            for (String webSocketHeader : request.getHeader(WEB_SOCKET_TOKEN_HEADER).split(",")) {
                if (jwtPattern.matcher(webSocketHeader).matches()) {
                    header = webSocketHeader;
                }
                else {
                    response.addHeader(WEB_SOCKET_TOKEN_HEADER, webSocketHeader);
                }
            };
        }
        return header.replace(TOKEN_PREFIX, "");
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String token) {
        if (token != null) {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET.getBytes())
                    .parseClaimsJws(token)
                    .getBody();
            String user = claims.getSubject();
            if (user != null) {
                List<GrantedAuthority> grantedAuthorities = null;
                List<String> roles = (List<String>) claims.get("roles");
                if (roles != null) {
                    grantedAuthorities = roles.stream().map(role -> new Authority(role)).collect(toList());
                }
                return new UsernamePasswordAuthenticationToken(user, null, grantedAuthorities);
            }
            return null;
        }
        return null;
    }

}
