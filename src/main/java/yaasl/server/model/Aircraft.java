package yaasl.server.model;

import javax.persistence.*;

import static javax.persistence.EnumType.ORDINAL;
import static javax.persistence.GenerationType.AUTO;

@Entity
public class Aircraft {

    private Long id;
    private String callSign;
    private boolean canTow;
    private boolean needsTowing;
    private int numberOfSeats;

    public Aircraft() {
    }

    public Aircraft(String callSign, boolean needsTowing, boolean canTow, int numberOfSeats) {
        this.callSign = callSign;
        this.needsTowing = needsTowing;
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

    public void setCallSign(String callSign) {
        this.callSign = callSign;
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

}
