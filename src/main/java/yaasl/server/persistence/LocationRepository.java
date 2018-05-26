package yaasl.server.persistence;

import org.springframework.data.repository.CrudRepository;
import yaasl.server.model.Aircraft;
import yaasl.server.model.Location;

import java.util.List;

public interface LocationRepository extends CrudRepository<Location, Long> {

    Location findByIcao(String name);

}
