package yaasl.server.model;

import javax.persistence.*;

import static javax.persistence.EnumType.ORDINAL;
import static javax.persistence.GenerationType.AUTO;

@Entity
public class Aircraft {

    private Long id;
    private String callSign;
    private boolean canTow;
    private int numberOfSeats;

    public Aircraft() {
    }

    public Aircraft(String callSign, boolean canTow, int numberOfSeats) {
        this.callSign = callSign;
        this.canTow = canTow;
        this.numberOfSeats = numberOfSeats;
    }

    @Id
    @GeneratedValue(strategy=AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCallSign() {
        return callSign;
    }

    public boolean isCanTow() {
        return canTow;
    }

    public void setCanTow(boolean canTow) {
        this.canTow = canTow;
    }

    public void setCallSign(String callSign) {
        this.callSign = callSign;
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(int numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

}
