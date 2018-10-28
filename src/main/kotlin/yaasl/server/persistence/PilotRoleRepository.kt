package yaasl.server.persistence

import org.springframework.data.repository.CrudRepository
import yaasl.server.model.Pilot
import yaasl.server.model.PilotRole

interface PilotRoleRepository : CrudRepository<PilotRole, Long> {

    fun findByDescription(description: String): PilotRole

}
