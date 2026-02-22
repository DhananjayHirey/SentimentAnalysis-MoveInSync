package com.moveinsync.sentimentProcessor.repository;

import com.moveinsync.sentimentProcessor.entity.DriverSentiment;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverSentimentRepository
        extends JpaRepository<DriverSentiment, String> {

    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO driver_sentiment 
        (driver_id, total_score, feedback_count, average_score, updated_at)
        VALUES (:driverId, :total, :count, :avg, NOW())
        ON CONFLICT (driver_id)
        DO UPDATE SET
            total_score = :total,
            feedback_count = :count,
            average_score = :avg,
            updated_at = NOW()
        """,
            nativeQuery = true)
    void upsert(
            @Param("driverId") String driverId,
            @Param("total") double total,
            @Param("count") int count,
            @Param("avg") double avg
    );
}