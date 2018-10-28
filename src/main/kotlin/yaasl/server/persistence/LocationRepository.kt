package yaasl.server.persistence

import org.springframework.data.repository.CrudRepository
import yaasl.server.model.Aircraft
import yaasl.server.model.Location

interface LocationRepository : CrudRepository<Location, Long> {

    fun findByIcao(name: String): Location?

}
