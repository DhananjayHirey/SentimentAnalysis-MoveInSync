package com.moveinsync.sentimentProcessor.repository;

import com.moveinsync.sentimentProcessor.entity.DriverFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DriverFeedbackRepository extends JpaRepository<DriverFeedback, Long> {
    List<DriverFeedback> findByDriverId(String driverId);
}
