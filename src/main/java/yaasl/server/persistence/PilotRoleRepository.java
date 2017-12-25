package yaasl.server.persistence;

import org.springframework.data.repository.CrudRepository;
import yaasl.server.model.Pilot;
import yaasl.server.model.PilotRole;

public interface PilotRoleRepository extends CrudRepository<PilotRole, Long> {

    PilotRole findByDescription(String description);

}
