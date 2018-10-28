package yaasl.server.persistence

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import yaasl.server.model.Feedback
import yaasl.server.model.Flight

interface FeedbackRepository : CrudRepository<Feedback, Long> {

    @Query("select feedback from Feedback feedback")
    fun findAllFeedback(): List<Feedback>

}
