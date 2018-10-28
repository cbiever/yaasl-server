package yaasl.server.model

import javax.persistence.*
import javax.persistence.GenerationType.AUTO

@Entity
class Pilot(@get:Id
            @get:GeneratedValue(strategy = AUTO)
            var id: Long? = null,
            var name: String? = null,
            @get:ManyToOne
            @get:JoinColumn(name = "pilot_role_id")
            var standardRole: PilotRole? = null,
            var isCanTow: Boolean = false)
