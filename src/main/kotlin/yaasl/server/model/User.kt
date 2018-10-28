package yaasl.server.model

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import javax.persistence.*
import javax.persistence.FetchType.EAGER
import javax.persistence.GenerationType.AUTO

@Entity
@Table(name = "yaasl_user")
data class User(@get:Id
                @get:GeneratedValue(strategy = AUTO)
                var id: Long? = null,
                private var username: String? = null,
                private var password: String? = null,
                var accountNonExpired: Boolean = true,
                var accountNonLocked: Boolean = true,
                var credentialsNonExpired: Boolean = true,
                var enabled: Boolean = true,
                @get:ManyToMany(targetEntity = Authority::class, fetch = EAGER)
                @get:JoinTable(name = "user_authority", joinColumns = arrayOf(JoinColumn(name = "user_id", referencedColumnName = "id")), inverseJoinColumns = arrayOf(JoinColumn(name = "authority_id", referencedColumnName = "id")))
                var grantedAuthorities: MutableSet<GrantedAuthority>? = null,
                var MD5: Boolean = false,
                @get:OneToMany(mappedBy = "user", fetch = EAGER)
                var tokens: MutableSet<RememberMeToken> = HashSet()) : UserDetails {

    @Transient
    override fun getAuthorities(): Set<GrantedAuthority> {
        return grantedAuthorities!!
    }

    fun addAuthority(authority: GrantedAuthority) {
        grantedAuthorities!!.add(authority)
    }

    override fun getUsername(): String? {
        return username
    }

    fun setUsername(username: String?) {
        this.username = username
    }

    override fun getPassword(): String? {
        return password
    }

    fun setPassword(password: String?) {
        this.password = password
    }

    @Transient
    override fun isAccountNonExpired(): Boolean {
        return accountNonExpired
    }

    @Transient
    override fun isAccountNonLocked(): Boolean {
        return accountNonLocked
    }

    @Transient
    override fun isCredentialsNonExpired(): Boolean {
        return credentialsNonExpired
    }

    @Transient
    override fun isEnabled(): Boolean {
        return enabled
    }

}
