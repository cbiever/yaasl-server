package yaasl.server.persistence;

import org.springframework.data.repository.CrudRepository;
import yaasl.server.model.Authority;

public interface AuthorityRepository extends CrudRepository<Authority, Long> {
}
