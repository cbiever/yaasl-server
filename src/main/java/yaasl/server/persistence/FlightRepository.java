package yaasl.server.persistence;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import yaasl.server.model.Flight;
import yaasl.server.model.Location;

import java.util.Date;
import java.util.List;

public interface FlightRepository extends CrudRepository<Flight, Long> {

    @Query("select flight from Flight flight")
    List<Flight> findAllFlights();

    @Query("select flight from Flight flight where flight.startLocation = ?1 or flight.landingLocation = ?1")
    List<Flight> findByLocation(Location location);

    @Query("select flight from Flight flight where (flight.startLocation = ?1 or flight.landingLocation = ?1) and (flight.startTime between ?2 and ?3 or flight.startTime is null)")
    List<Flight> findByLocationAndDate(Location location, Date beginningOfDate, Date endOfDay);

}
