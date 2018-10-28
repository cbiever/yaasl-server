package yaasl.server.model

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.AUTO
import javax.persistence.Id
import javax.persistence.Transient

@Entity
data class Aircraft(@Id @GeneratedValue(strategy = AUTO)
                    var id: Long? = null,
                    var callSign: String? = null,
                    @Transient
                    var competitionNumber: String? = null,
                    var canTow: Boolean = false,
                    var needsTowing: Boolean = false,
                    var numberOfSeats: Int = 0,
                    @Transient @JsonIgnore
                    var identifier: String? = null,
                    @Transient @JsonIgnore
                    var towIdentifier: String? = null)