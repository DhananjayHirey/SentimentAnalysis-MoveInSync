package com.moveinsync.sentimentProcessor.repository;

import com.moveinsync.sentimentProcessor.entity.TripFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TripFeedbackRepository extends JpaRepository<TripFeedback, Long> {
    List<TripFeedback> findByTripId(String tripId);
}
