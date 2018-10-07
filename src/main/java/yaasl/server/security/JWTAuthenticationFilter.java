package yaasl.server.security;

import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import yaasl.server.model.Authority;
import yaasl.server.model.User;
import yaasl.server.persistence.TemporaryAuthorityRepository;
import yaasl.server.persistence.UserRepository;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

import static io.jsonwebtoken.SignatureAlgorithm.HS512;
import static java.lang.Boolean.parseBoolean;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static yaasl.server.security.SecurityConstants.EXPIRATION_TIME;
import static yaasl.server.security.SecurityConstants.TOKEN_PREFIX;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private TemporaryAuthorityRepository temporaryAuthorityRepository;
    private PasswordEncoder passwordEncoder;
    private PersistentTokenBasedRememberMeServices rememberMeServices;
    private String jwtSecret;

    public JWTAuthenticationFilter(String jwtSecret, AuthenticationManager authenticationManager,
                                   UserRepository userRepository,
                                   TemporaryAuthorityRepository temporaryAuthorityRepository,
                                   PasswordEncoder passwordEncoder,
                                   PersistentTokenBasedRememberMeServices rememberMeServices) {
        super();
        this.jwtSecret = jwtSecret;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.temporaryAuthorityRepository = temporaryAuthorityRepository;
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
        List<String> roles = authentication
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(toList());
        List<Authority> temporaryAuthorities = temporaryAuthorityRepository.findByUserAndDate((User) authentication.getPrincipal(), new Date());
        roles.addAll(temporaryAuthorities.stream().map(authority -> authority.getAuthority()).collect(toList()));
        String token = Jwts.builder()
                .setSubject(((User) authentication.getPrincipal()).getUsername())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .claim("roles", roles.toArray())
                .signWith(HS512, jwtSecret.getBytes())
                .compact();
        response.addHeader(AUTHORIZATION, TOKEN_PREFIX + token);
    }

}