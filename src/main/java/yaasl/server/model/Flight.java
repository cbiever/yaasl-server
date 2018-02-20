package yaasl.server.model;

import javax.persistence.*;
import java.util.Date;

import static javax.persistence.GenerationType.AUTO;

@Entity
public class Flight {

    private Long id;
    private Date startTime;
    private Location startLocation;
    private Date landingTime;
    private Location landingLocation;
    private Aircraft aircraft;
    private Pilot pilot1;
    private PilotRole pilot1Role;
    private Pilot pilot2;
    private PilotRole pilot2Role;
    private Pilot towPilot;
    private Aircraft towPlane;
    private Date towPlaneLandingTime;
    private CostSharing costSharing;
    private String comment;
    private boolean locked;

    public Flight() {
    }

    @Id
    @GeneratedValue(strategy=AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @ManyToOne
    @JoinColumn(name="start_location_id")
    public Location getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(Location startLocation) {
        this.startLocation = startLocation;
    }

    public Date getLandingTime() {
        return landingTime;
    }

    public void setLandingTime(Date landingTime) {
        this.landingTime = landingTime;
    }

    @ManyToOne
    @JoinColumn(name="landing_location_id")
    public Location getLandingLocation() {
        return landingLocation;
    }

    public void setLandingLocation(Location landingLocation) {
        this.landingLocation = landingLocation;
    }

    @ManyToOne
    @JoinColumn(name = "aircraft_id")
    public Aircraft getAircraft() {
        return aircraft;
    }

    public void setAircraft(Aircraft aircraft) {
        this.aircraft = aircraft;
    }

    @ManyToOne
    @JoinColumn(name = "pilot1_id")
    public Pilot getPilot1() {
        return pilot1;
    }

    public void setPilot1(Pilot pilot1) {
        this.pilot1 = pilot1;
    }

    @ManyToOne
    @JoinColumn(name = "pilot1_role_id")
    public PilotRole getPilot1Role() {
        return pilot1Role;
    }

    public void setPilot1Role(PilotRole pilot1Role) {
        this.pilot1Role = pilot1Role;
    }

    @ManyToOne
    @JoinColumn(name = "pilot2_id")
    public Pilot getPilot2() {
        return pilot2;
    }

    public void setPilot2(Pilot pilot2) {
        this.pilot2 = pilot2;
    }

    @ManyToOne
    @JoinColumn(name = "pilot2_role_id")
    public PilotRole getPilot2Role() {
        return pilot2Role;
    }

    public void setPilot2Role(PilotRole pilot2Role) {
        this.pilot2Role = pilot2Role;
    }

    @ManyToOne
    @JoinColumn(name = "tow_plane_id")
    public Aircraft getTowPlane() {
        return towPlane;
    }

    public void setTowPlane(Aircraft towPlane) {
        this.towPlane = towPlane;
    }

    @ManyToOne
    @JoinColumn(name = "tow_pilot_id")
    public Pilot getTowPilot() {
        return towPilot;
    }

    public void setTowPilot(Pilot towPilot) {
        this.towPilot = towPilot;
    }

    public Date getTowPlaneLandingTime() {
        return towPlaneLandingTime;
    }

    public void setTowPlaneLandingTime(Date towPlaneLandingTime) {
        this.towPlaneLandingTime = towPlaneLandingTime;
    }

    @ManyToOne
    @JoinColumn(name = "cost_sharing_id")
    public CostSharing getCostSharing() {
        return costSharing;
    }

    public void setCostSharing(CostSharing costSharing) {
        this.costSharing = costSharing;
    }

    @Column(length = 300)
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

}
