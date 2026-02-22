package com.moveinsync.feedbackService.repository;

import com.moveinsync.feedbackService.entity.MarshalFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarshalFeedbackRepository extends JpaRepository<MarshalFeedback, Long> {
    List<MarshalFeedback> findByMarshalId(String marshalId);

    @Query("SELECT AVG(f.rating) FROM MarshalFeedback f WHERE f.marshalId = :marshalId")
    Double findAverageRatingByMarshalId(String marshalId);

    @Query("SELECT f.marshalId, AVG(f.rating) FROM MarshalFeedback f GROUP BY f.marshalId")
    List<Object[]> findAllMarshalsAverageRatings();
}
