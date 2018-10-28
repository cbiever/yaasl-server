package yaasl.server.persistence

import org.springframework.data.repository.CrudRepository
import yaasl.server.model.Pilot

interface PilotRepository : CrudRepository<Pilot, Long>
