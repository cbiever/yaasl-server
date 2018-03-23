package yaasl.server.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import yaasl.server.model.RememberMeToken;
import yaasl.server.model.User;

@Component
public interface RememberMeTokenRepository extends CrudRepository<RememberMeToken, Long> {

    RememberMeToken findBySeries(String seriesId);

}
