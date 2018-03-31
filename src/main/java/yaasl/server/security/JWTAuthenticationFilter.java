package yaasl.server.security;

import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import yaasl.server.model.User;
import yaasl.server.persistence.UserRepository;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

import static io.jsonwebtoken.SignatureAlgorithm.HS512;
import static java.lang.Boolean.parseBoolean;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static yaasl.server.security.SecurityConstants.*;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private PersistentTokenBasedRememberMeServices rememberMeServices;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder passwordEncoder, PersistentTokenBasedRememberMeServices rememberMeServices) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.rememberMeServices = rememberMeServices;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        Authentication authentication = rememberMeServices.autoLogin(request, response);
        if (authentication == null) {
            String username = request.getParameter(SPRING_SECURITY_FORM_USERNAME_KEY);
            String password = request.getParameter(SPRING_SECURITY_FORM_PASSWORD_KEY);
            if (isNotEmpty(username) && isNotEmpty(password)) {
                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
                authentication = authenticationManager.authenticate(token);
                if (authentication != null) {
                    User user = userRepository.findByUsername(username);
                    if (user.isMD5()) {
                        user.setPassword(passwordEncoder.encode(password));
                        user.setMD5(false);
                        userRepository.save(user);
                        token = new UsernamePasswordAuthenticationToken(username, password);
                        authentication = authenticationManager.authenticate(token);
                    }
                    if (parseBoolean(request.getParameter("rememberMe"))) {
                        rememberMeServices.loginSuccess(request, response, authentication);
                    }
                    else {
                        rememberMeServices.logout(request, response, authentication);
                    }
                }
            }
        }
        return authentication;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {
        String[] roles = authentication
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toArray(String[]::new);
        String token = Jwts.builder()
                .setSubject(((User) authentication.getPrincipal()).getUsername())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .claim("roles", roles)
                .signWith(HS512, SECRET.getBytes())
                .compact();
        response.addHeader(TOKEN_HEADER, TOKEN_PREFIX + token);
    }

}