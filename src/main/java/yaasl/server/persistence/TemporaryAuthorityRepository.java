package yaasl.server.persistence;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import yaasl.server.model.Authority;
import yaasl.server.model.TemporaryAuthority;
import yaasl.server.model.User;

import java.util.Date;
import java.util.List;

public interface TemporaryAuthorityRepository extends CrudRepository<TemporaryAuthority, Long> {

    @Query("select ta.authority from TemporaryAuthority ta where ta.user = ?1 and ta.date = ?2")
    List<Authority> findByUserAndDate(User user, Date date);

}
