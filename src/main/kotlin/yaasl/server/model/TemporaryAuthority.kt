package yaasl.server.model

import java.util.*
import javax.persistence.*
import javax.persistence.GenerationType.AUTO

@Entity
class TemporaryAuthority(@get:Id
                         @get:GeneratedValue(strategy = AUTO)
                         var id: Long? = null,
                         @get:ManyToOne
                         @get:JoinColumn(name = "authority_id")
                         var authority: Authority? = null,
                         @get:Temporal(TemporalType.DATE)
                         var date: Date? = null,
                         @get:ManyToOne
                         @get:JoinColumn(name = "user_id")
                         var user: User? = null)
