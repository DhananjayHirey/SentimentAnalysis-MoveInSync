package com.moveinsync.feedbackService.repository;

import com.moveinsync.feedbackService.entity.DriverFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriverFeedbackRepository extends JpaRepository<DriverFeedback, Long> {
    List<DriverFeedback> findByDriverId(String driverId);

    @Query("SELECT AVG(f.rating) FROM DriverFeedback f WHERE f.driverId = :driverId")
    Double findAverageRatingByDriverId(String driverId);

    @Query("SELECT f.driverId, AVG(f.rating) FROM DriverFeedback f GROUP BY f.driverId")
    List<Object[]> findAllDriversAverageRatings();
}
