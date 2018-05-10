package yaasl.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

import static javax.persistence.GenerationType.AUTO;

@Entity
public class Aircraft {

    private Long id;
    private String callSign;
    private String competitionNumber;
    private boolean canTow;
    private boolean needsTowing;
    private int numberOfSeats;
    private String identifier;
    private String towIdentifier;

    public Aircraft() {
    }

    @Id
    @GeneratedValue(strategy = AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCallSign() {
        return callSign;
    }

    public void setCallSign(String callSign) {
        this.callSign = callSign;
    }

    @Transient
    public String getCompetitionNumber() {
        return competitionNumber;
    }

    public void setCompetitionNumber(String competitionNumber) {
        this.competitionNumber = competitionNumber;
    }

    public boolean isCanTow() {
        return canTow;
    }

    public void setCanTow(boolean canTow) {
        this.canTow = canTow;
    }

    public boolean isNeedsTowing() {
        return needsTowing;
    }

    public void setNeedsTowing(boolean needsTowing) {
        this.needsTowing = needsTowing;
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(int numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    @Transient
    @JsonIgnore
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Transient
    @JsonIgnore
    public String getTowIdentifier() {
        return towIdentifier;
    }

    public void setTowIdentifier(String towIdentifier) {
        this.towIdentifier = towIdentifier;
    }

}
