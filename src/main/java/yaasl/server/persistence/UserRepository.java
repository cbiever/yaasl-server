package yaasl.server.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import yaasl.server.model.User;

@Component
public interface UserRepository extends CrudRepository<User, Long> {

    User findByUsername(String username);

}
