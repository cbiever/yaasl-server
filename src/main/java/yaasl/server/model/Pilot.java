package yaasl.server.model;

import javax.persistence.*;

import java.util.Set;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.GenerationType.AUTO;

@Entity
public class Pilot {

    private Long id;
    private String name;
    private PilotRole standardRole;

    public Pilot() {
    }

    public Pilot(String name, PilotRole standardRole) {
        this.name = name;
        this.standardRole = standardRole;
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

    @ManyToOne
    @JoinColumn(name = "pilot_role_id")
    public PilotRole getStandardRole() {
        return standardRole;
    }

    public void setStandardRole(PilotRole standardRole) {
        this.standardRole = standardRole;
    }

    //    @OneToMany(mappedBy = "flight", cascade = ALL)
//    public Set<Flight> getFlights() {
//        return flights;
//    }

//    public void setFlights(Set<Flight> flights) {
//        this.flights = flights;
//    }

}
