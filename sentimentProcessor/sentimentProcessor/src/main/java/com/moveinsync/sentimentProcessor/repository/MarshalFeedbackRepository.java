package com.moveinsync.sentimentProcessor.repository;

import com.moveinsync.sentimentProcessor.entity.MarshalFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MarshalFeedbackRepository extends JpaRepository<MarshalFeedback, Long> {
    List<MarshalFeedback> findByMarshalId(String marshalId);
}
