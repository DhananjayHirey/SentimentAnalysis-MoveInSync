package com.moveinsync.feedbackService.repository;

import com.moveinsync.feedbackService.entity.AppFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppFeedbackRepository extends JpaRepository<AppFeedback, Long> {
    List<AppFeedback> findByUserId(String userId);

    @Query("SELECT AVG(f.rating) FROM AppFeedback f")
    Double findAverageRating();
}
