package yaasl.server.security

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository
import org.springframework.stereotype.Component
import yaasl.server.model.Authority
import yaasl.server.model.RememberMeToken
import yaasl.server.model.User
import yaasl.server.persistence.RememberMeTokenRepository
import yaasl.server.persistence.UserRepository
import yaasl.server.security.YaaslUserDetailsService.ENCRYPTION_METHOD.BCRYPT
import java.util.*

@Component
class YaaslUserDetailsService(private val userRepository: UserRepository,
                              private val rememberMeTokenRepository: RememberMeTokenRepository,
                              private val passwordEncoder: PasswordEncoder) : UserDetailsService, PersistentTokenRepository {

    enum class ENCRYPTION_METHOD {
        MD5, BCRYPT
    }

    override fun loadUserByUsername(username: String): UserDetails {
        return userRepository.findByUsername(username) ?: throw UsernameNotFoundException("User $username not found")
    }

    override fun createNewToken(token: PersistentRememberMeToken) {
        val user = userRepository.findByUsername(token.username)
        val rememberMeToken = RememberMeToken().apply { tokenValue = token.tokenValue; this.user = user }
        rememberMeTokenRepository.save(rememberMeToken)
    }

    override fun updateToken(series: String, tokenValue: String, lastUsed: Date) {
        val rememberMeToken = rememberMeTokenRepository.findBySeries(series)
        if (rememberMeToken != null) {
            rememberMeToken.tokenValue = tokenValue
            rememberMeToken.date = lastUsed
            rememberMeTokenRepository.save(rememberMeToken)
        }
    }

    override fun getTokenForSeries(series: String): PersistentRememberMeToken? {
        val rememberMeToken = rememberMeTokenRepository.findBySeries(series)
        return if (rememberMeToken != null) {
            PersistentRememberMeToken(rememberMeToken.username, rememberMeToken.series, rememberMeToken.tokenValue, rememberMeToken.date)
        } else {
            null
        }
    }

    override fun removeUserTokens(username: String) {
        val user = userRepository.findByUsername(username)
        if (user != null) {
            user.tokens.clear()
            userRepository.save(user)
        }
    }

    fun addUser(username: String, password: String, vararg authorities: Authority) {
        addUser(username, password, BCRYPT, *authorities)
    }

    fun addUser(username: String, password: String, encryption_method: ENCRYPTION_METHOD, vararg authorities: Authority) {
        val user = User()
        user.username = username
        if (encryption_method == BCRYPT) {
            user.password = passwordEncoder.encode(password)
        } else {
            user.password = password
            user.MD5 = true
        }
        for (authority in authorities) {
            user.addAuthority(authority)
        }
        userRepository.save(user)
    }

    fun numberOfUsers(): Long {
        return userRepository.count()
    }

}
