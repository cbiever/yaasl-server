package yaasl.server.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.AUTO
import javax.persistence.Id

@Entity
data class Feedback(@Id @GeneratedValue(strategy = AUTO)
                    var id: Long? = null,
                    var feedback: String? = null,
                    var comment: String? = null)
