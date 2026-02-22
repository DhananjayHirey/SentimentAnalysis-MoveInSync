package com.moveinsync.sentimentProcessor.repository;

import com.moveinsync.sentimentProcessor.entity.AppFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AppFeedbackRepository extends JpaRepository<AppFeedback, Long> {
    List<AppFeedback> findByUserId(String userId);
}
