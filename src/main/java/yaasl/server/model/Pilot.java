package yaasl.server.model;

import javax.persistence.*;

import java.util.Set;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.GenerationType.AUTO;

@Entity
public class Pilot {

    private Long id;
    private String name;
    private PilotRole role;
    private boolean frontSeat;
//    private Set<Flight> flights;

    public Pilot() {
    }

    public Pilot(String name) {
        this.name = name;
    }

    @Id
    @GeneratedValue(strategy=AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PilotRole getRole() {
        return role;
    }

    public void setRole(PilotRole role) {
        this.role = role;
    }

    public boolean isFrontSeat() {
        return frontSeat;
    }

    public void setFrontSeat(boolean frontSeat) {
        this.frontSeat = frontSeat;
    }

//    @OneToMany(mappedBy = "flight", cascade = ALL)
//    public Set<Flight> getFlights() {
//        return flights;
//    }

//    public void setFlights(Set<Flight> flights) {
//        this.flights = flights;
//    }

}
