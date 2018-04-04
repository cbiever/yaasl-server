package yaasl.server.persistence;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import yaasl.server.model.Feedback;
import yaasl.server.model.Flight;

import java.util.List;

public interface FeedbackRepository extends CrudRepository<Feedback, Long> {

    @Query("select feedback from Feedback feedback")
    List<Feedback> findAllFeedback();

}
