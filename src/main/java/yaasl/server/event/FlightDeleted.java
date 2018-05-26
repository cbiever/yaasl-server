package yaasl.server.event;

import yaasl.server.model.Flight;

public class FlightDeleted {

    private Flight flight;

    public FlightDeleted(Flight flight) {
        this.flight = flight;
    }

    public Flight getFlight() {
        return flight;
    }

}
