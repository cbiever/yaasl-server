package yaasl.server.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.AUTO
import javax.persistence.Id

@Entity
class PilotRole(@get:Id
                @get:GeneratedValue(strategy = AUTO)
                var id: Long? = null,
                var description: String? = null,
                var i18n: String? = null)
