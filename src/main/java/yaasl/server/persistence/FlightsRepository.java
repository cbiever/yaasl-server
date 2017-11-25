package yaasl.server.persistence;

import org.springframework.data.repository.CrudRepository;
import yaasl.server.model.Flight;

public interface FlightsRepository extends CrudRepository<Flight, Long> {
}
