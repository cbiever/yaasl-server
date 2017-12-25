package yaasl.server.persistence;

import org.springframework.data.repository.CrudRepository;
import yaasl.server.model.Pilot;
import yaasl.server.model.PilotRole;

import java.util.List;

public interface PilotsRepository extends CrudRepository<Pilot, Long> {
}
