package yaasl.server.persistence;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import yaasl.server.model.Aircraft;

public interface AircraftRepository extends CrudRepository<Aircraft, Long> {

    @Query("select aircraft from Aircraft aircraft where aircraft.callSign = ?1")
    Aircraft findAircraftByCallSign(String callSign);

}
