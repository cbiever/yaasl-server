package yaasl.server.security

import org.apache.commons.codec.binary.Hex.encodeHexString
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import java.security.MessageDigest

@Component
class PasswordEncoder: org.springframework.security.crypto.password.PasswordEncoder {

    private val bCryptPasswordEncoder = BCryptPasswordEncoder()
    private val md5Digest: MessageDigest

    init {
        this.md5Digest = MessageDigest.getInstance("md5")
    }

    override fun encode(rawPassword: CharSequence): String {
        return bCryptPasswordEncoder.encode(rawPassword)
    }

    override fun matches(rawPassword: CharSequence, encodedPassword: String): Boolean {
        return if (bCryptPasswordEncoder.matches(rawPassword, encodedPassword)) {
            true
        } else {
            encodedPassword == encodeHexString(md5Digest.digest(rawPassword.toString().toByteArray()))
        }
    }

}
