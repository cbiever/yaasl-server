package yaasl.server.persistence;

import org.springframework.data.repository.CrudRepository;
import yaasl.server.model.Aircraft;
import yaasl.server.model.Flight;

public interface AircraftRepository extends CrudRepository<Aircraft, Long> {
}
