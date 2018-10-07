package yaasl.server.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Component;
import yaasl.server.model.Authority;
import yaasl.server.model.RememberMeToken;
import yaasl.server.model.User;
import yaasl.server.persistence.RememberMeTokenRepository;
import yaasl.server.persistence.UserRepository;

import java.util.Date;

import static yaasl.server.security.YaaslUserDetailsService.ENCRYPTION_METHOD.BCRYPT;

@Component
public class YaaslUserDetailsService implements UserDetailsService, PersistentTokenRepository {

    public enum ENCRYPTION_METHOD { MD5, BCRYPT };

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RememberMeTokenRepository rememberMeTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User " + username + " not found");
        }
        return user;
    }

    public void addUser(String username, String password, Authority... authorities) {
        addUser(username, password, BCRYPT, authorities);
    }

    public void addUser(String username, String password, ENCRYPTION_METHOD encryption_method, Authority... authorities) {
        User user = new User();
        user.setUsername(username);
        if (encryption_method == BCRYPT) {
            user.setPassword(passwordEncoder.encode(password));
        }
        else {
            user.setPassword(password);
            user.setMD5(true);
        }
        for (Authority authority : authorities) {
            user.addAuthority(authority);
        }
        userRepository.save(user);
    }

    public long numberOfUsers() {
        return userRepository.count();
    }

    @Override
    public void createNewToken(PersistentRememberMeToken token) {
        User user = userRepository.findByUsername(token.getUsername());
        RememberMeToken rememberMeToken = new RememberMeToken(token, user);
        rememberMeTokenRepository.save(rememberMeToken);
    }

    @Override
    public void updateToken(String series, String tokenValue, Date lastUsed) {
        RememberMeToken rememberMeToken = rememberMeTokenRepository.findBySeries(series);
        if (rememberMeToken != null) {
            rememberMeToken.setTokenValue(tokenValue);
            rememberMeToken.setDate(lastUsed);
            rememberMeTokenRepository.save(rememberMeToken);
        }
    }

    @Override
    public PersistentRememberMeToken getTokenForSeries(String series) {
        RememberMeToken rememberMeToken = rememberMeTokenRepository.findBySeries(series);
        if (rememberMeToken != null) {
            return new PersistentRememberMeToken(rememberMeToken.getUsername(), rememberMeToken.getSeries(), rememberMeToken.getTokenValue(), rememberMeToken.getDate());
        }
        else {
            return null;
        }
    }

    @Override
    public void removeUserTokens(String username) {
        User user = userRepository.findByUsername(username);
        user.setTokens(null);
        userRepository.save(user);
    }

}
