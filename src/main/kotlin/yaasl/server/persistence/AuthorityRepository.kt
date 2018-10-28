package yaasl.server.persistence

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import yaasl.server.model.Authority
import yaasl.server.model.Flight
import yaasl.server.model.Location

interface AuthorityRepository : CrudRepository<Authority, Long> {

    @Query("select authority from Authority authority where authority.authority = ?1")
    fun findByName(name: String): Authority

}
