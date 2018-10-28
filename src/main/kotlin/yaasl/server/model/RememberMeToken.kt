package yaasl.server.model

import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.AUTO
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.FetchType.LAZY

@Entity
class RememberMeToken(@get:Id
                      @get:GeneratedValue(strategy = AUTO)
                      var id: Long? = null,
                      var username: String? = null,
                      var series: String? = null,
                      var tokenValue: String? = null,
                      var date: Date? = null,
                      @get:ManyToOne(fetch = LAZY)
                      var user: User? = null)
