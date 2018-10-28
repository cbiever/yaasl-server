package yaasl.server.persistence

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Component
import yaasl.server.model.RememberMeToken
import yaasl.server.model.User

@Component
interface RememberMeTokenRepository : CrudRepository<RememberMeToken, Long> {

    fun findBySeries(seriesId: String): RememberMeToken?

}
