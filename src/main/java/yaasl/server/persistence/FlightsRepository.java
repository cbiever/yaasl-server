package yaasl.server.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import yaasl.server.model.Flight;
import yaasl.server.model.Location;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;

public interface FlightsRepository extends CrudRepository<Flight, Long> {

    @Query("select flight from Flight flight where flight.startLocation = ?1 or flight.landingLocation = ?1")
    List<Flight> findByLocation(Location location);

    @Query("select flight from Flight flight where (flight.startLocation = ?1 or flight.landingLocation = ?1) and (flight.startTime between ?2 and ?3 or flight.startTime is null)")
    List<Flight> findFlights(Location location, Date beginningOfDate, Date endOfDay);

}
