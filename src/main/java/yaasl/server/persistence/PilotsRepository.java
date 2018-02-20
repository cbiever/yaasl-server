package yaasl.server.persistence;

import org.springframework.data.repository.CrudRepository;
import yaasl.server.model.Pilot;

public interface PilotsRepository extends CrudRepository<Pilot, Long> {
}
