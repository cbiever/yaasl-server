package yaasl.server.model

import java.util.*
import javax.persistence.*
import javax.persistence.GenerationType.AUTO

@Entity
data class Flight(@Id @GeneratedValue(strategy = AUTO)
                  var id: Long? = null,
                  var startTime: Date? = null,
                  @ManyToOne @JoinColumn(name = "start_location_id")
                  var startLocation: Location? = null,
                  var landingTime: Date? = null,
                  @ManyToOne @JoinColumn(name = "landing_location_id")
                  var landingLocation: Location? = null,
                  @ManyToOne @JoinColumn(name = "aircraft_id")
                  var aircraft: Aircraft? = null,
                  @ManyToOne @JoinColumn(name = "pilot1_id")
                  var pilot1: Pilot? = null,
                  @ManyToOne @JoinColumn(name = "pilot1_role_id")
                  var pilot1Role: PilotRole? = null,
                  @ManyToOne @JoinColumn(name = "pilot2_id")
                  var pilot2: Pilot? = null,
                  @ManyToOne @JoinColumn(name = "pilot2_role_id")
                  var pilot2Role: PilotRole? = null,
                  @ManyToOne @JoinColumn(name = "tow_pilot_id")
                  var towPilot: Pilot? = null,
                  @ManyToOne @JoinColumn(name = "tow_plane_id")
                  var towPlane: Aircraft? = null,
                  var towPlaneLandingTime: Date? = null,
                  @ManyToOne @JoinColumn(name = "cost_sharing_id")
                  var costSharing: CostSharing? = null,
                  @Column(length = 300)
                  var comment: String? = null,
                  var editable: Boolean? = false,
                  var locked: Boolean? = false,
                  var revision: Long? = 0,
                  @Transient
                  var sequence: Long = 0)
