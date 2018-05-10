package yaasl.server.security;

import org.apache.commons.codec.binary.Hex;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;

import static org.apache.commons.codec.binary.Hex.encodeHexString;

@Component
public class PasswordEncoder implements org.springframework.security.crypto.password.PasswordEncoder {

    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    private MessageDigest md5Digest;

    public PasswordEncoder() throws Exception {
        this.md5Digest = MessageDigest.getInstance("md5");
    }

    @Override
    public String encode(CharSequence rawPassword) {
        return bCryptPasswordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (bCryptPasswordEncoder.matches(rawPassword, encodedPassword)) {
            return true;
        }
        else {
            return encodedPassword.equals(encodeHexString(md5Digest.digest(rawPassword.toString().getBytes())));
        }
    }

}
