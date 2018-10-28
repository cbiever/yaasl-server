package yaasl.server.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.AUTO
import javax.persistence.Id

@Entity
class Location(@get:Id
               @get:GeneratedValue(strategy = AUTO)
               var id: Long? = null,
               var icao: String? = null,
               var name: String? = null)
