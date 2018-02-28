package yaasl.server.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import yaasl.server.model.Authority;
import yaasl.server.model.User;
import yaasl.server.persistence.UserRepository;

import java.util.Collections;
import java.util.Set;

import static yaasl.server.security.UserService.ENCRYPTION_METHOD.BCRYPT;

@Component
public class UserService implements UserDetailsService {

    public enum ENCRYPTION_METHOD { MD5, BCRYPT };

    @Autowired
    private UserRepository userRepository;

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

}
