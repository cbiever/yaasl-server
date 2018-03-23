package yaasl.server.export;

import yaasl.server.model.Flight;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.Date;
import java.util.List;

public class Data {

    private List<Flight> flights;
    private String location;
    private Date date;

    public Data(List<Flight> flights, String location, Date date) {
        this.flights = flights;
        this.location = location;
        this.date = date;
    }

    @XmlElementWrapper(name = "flights")
    @XmlElement(name = "flight")
    public List<Flight> getFlights() {
        return flights;
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
