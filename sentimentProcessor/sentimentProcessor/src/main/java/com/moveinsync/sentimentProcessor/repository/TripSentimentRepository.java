package com.moveinsync.sentimentProcessor.repository;

import com.moveinsync.sentimentProcessor.entity.TripSentiment;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TripSentimentRepository
        extends JpaRepository<TripSentiment, String> {

    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO trip_sentiment
            (trip_id, total_rating_sum, feedback_count, average_rating, positive_count, neutral_count, negative_count, updated_at)
            VALUES (:tripId, :totalSum, :count, :avg, :pos, :neu, :neg, NOW())
            ON CONFLICT (trip_id)
            DO UPDATE SET
                total_rating_sum = :totalSum,
                feedback_count = :count,
                average_rating = :avg,
                positive_count = :pos,
                neutral_count = :neu,
                negative_count = :neg,
                updated_at = NOW()
            """, nativeQuery = true)
    void upsert(
            @Param("tripId") String tripId,
            @Param("totalSum") double totalSum,
            @Param("count") int count,
            @Param("avg") double avg,
            @Param("pos") int pos,
            @Param("neu") int neu,
            @Param("neg") int neg);
}
