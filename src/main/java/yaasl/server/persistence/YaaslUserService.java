package yaasl.server.persistence;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import yaasl.server.auth.YaaslUser;

@Component
public class YaaslUserService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        YaaslUser yaaslUser = new YaaslUser();
yaaslUser.setUsername(username);
yaaslUser.setPassword(new BCryptPasswordEncoder().encode("test"));
        return yaaslUser;
    }

}
