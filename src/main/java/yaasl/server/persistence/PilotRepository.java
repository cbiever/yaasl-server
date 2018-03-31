package yaasl.server.persistence;

import org.springframework.data.repository.CrudRepository;
import yaasl.server.model.Pilot;

public interface PilotRepository extends CrudRepository<Pilot, Long> {
}
