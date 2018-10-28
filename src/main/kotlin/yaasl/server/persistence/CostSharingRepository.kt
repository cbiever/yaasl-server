package yaasl.server.persistence

import org.springframework.data.repository.CrudRepository
import yaasl.server.model.CostSharing
import yaasl.server.model.PilotRole

interface CostSharingRepository : CrudRepository<CostSharing, Long> {

    fun findByDescription(description: String): CostSharing

}
