package yaasl.server.persistence;

import org.springframework.data.repository.CrudRepository;
import yaasl.server.model.CostSharing;
import yaasl.server.model.PilotRole;

public interface CostSharingRepository extends CrudRepository<CostSharing, Long> {

    CostSharing findByDescription(String description);

}
