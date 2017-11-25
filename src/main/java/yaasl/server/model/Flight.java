package yaasl.server.model;

import javax.persistence.*;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

import static javax.persistence.GenerationType.AUTO;

@Entity
public class Flight {

    private Long id;
    private Aircraft aircraft;
    private Pilot pilot1;
    private Pilot pilot2;
    private OffsetDateTime startTime;
    private OffsetDateTime landingTime;

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

    public OffsetDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(OffsetDateTime startTime) {
        this.startTime = startTime;
    }

    public OffsetDateTime getLandingTime() {
        return landingTime;
    }

    public void setLandingTime(OffsetDateTime landingTime) {
        this.landingTime = landingTime;
    }

}
