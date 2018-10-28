package yaasl.server.model

import org.springframework.security.core.GrantedAuthority
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.AUTO
import javax.persistence.Id

@Entity
class Authority(@get:Id
                @get:GeneratedValue(strategy = AUTO)
                var id: Long? = null,
                private var authority: String? = null) : GrantedAuthority {

    override fun getAuthority(): String? {
        return authority
    }

    fun setAuthority(authority: String?) {
        this.authority = authority
    }

}
