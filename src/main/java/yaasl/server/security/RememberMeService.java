package yaasl.server.security;

import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;

import static yaasl.server.security.SecurityConstants.REMEMBER_ME_COOKIE;
import static yaasl.server.security.SecurityConstants.REMEMBER_ME_EXPIRATION_TIME;

public class RememberMeService extends PersistentTokenBasedRememberMeServices {

    public RememberMeService(String key, YaaslUserDetailsService yaaslUserDetailsService) {
        super(key, yaaslUserDetailsService, yaaslUserDetailsService);
        setParameter("rememberMe");
        setCookieName(REMEMBER_ME_COOKIE);
        setTokenValiditySeconds(REMEMBER_ME_EXPIRATION_TIME);
        setUseSecureCookie(true);
    }

}
