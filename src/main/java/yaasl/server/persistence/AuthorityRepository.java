package yaasl.server.persistence;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import yaasl.server.model.Authority;
import yaasl.server.model.Flight;
import yaasl.server.model.Location;

import java.util.List;

public interface AuthorityRepository extends CrudRepository<Authority, Long> {

    @Query("select authority from Authority authority where authority.authority = ?1")
    Authority findByName(String name);

}
