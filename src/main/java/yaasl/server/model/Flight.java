package yaasl.server.model;

import org.apache.tomcat.jni.Local;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.UUID;

import static javax.persistence.GenerationType.AUTO;

@Entity
public class Flight {

    private Long id;
    private Location location;
    private Aircraft aircraft;
    private Pilot pilot1;
    private Pilot pilot2;
    private Date startTime;
    private Date landingTime;

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

    @ManyToOne
    @JoinColumn(name="location_id")
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
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
    @JoinColumn(name = "pilot2_id")
    public Pilot getPilot2() {
        return pilot2;
    }

    public void setPilot2(Pilot pilot2) {
        this.pilot2 = pilot2;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getLandingTime() {
        return landingTime;
    }

    public void setLandingTime(Date landingTime) {
        this.landingTime = landingTime;
    }

}
