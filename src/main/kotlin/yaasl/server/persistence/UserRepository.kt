package yaasl.server.persistence

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Component
import yaasl.server.model.User

@Component
interface UserRepository : CrudRepository<User, Long> {

    fun findByUsername(username: String): User?

}
