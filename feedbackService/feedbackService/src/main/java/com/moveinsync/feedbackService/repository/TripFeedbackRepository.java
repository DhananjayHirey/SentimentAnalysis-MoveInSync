package com.moveinsync.feedbackService.repository;

import com.moveinsync.feedbackService.entity.TripFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripFeedbackRepository extends JpaRepository<TripFeedback, Long> {
    List<TripFeedback> findByTripId(String tripId);

    @Query("SELECT AVG(f.rating) FROM TripFeedback f WHERE f.tripId = :tripId")
    Double findAverageRatingByTripId(String tripId);

    @Query("SELECT f.tripId, AVG(f.rating) FROM TripFeedback f GROUP BY f.tripId")
    List<Object[]> findAllTripsAverageRatings();
}
