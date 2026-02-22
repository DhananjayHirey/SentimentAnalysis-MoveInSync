package com.moveinsync.sentimentProcessor.controller;

import com.moveinsync.sentimentProcessor.entity.*;
import com.moveinsync.sentimentProcessor.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/api/feedback/admin")
public class AdminController {

    private final DriverFeedbackRepository driverRepo;
    private final TripFeedbackRepository tripRepo;
    private final MarshalFeedbackRepository marshalRepo;
    private final AppFeedbackRepository appRepo;
    private final DriverSentimentRepository snapshotRepo;
    private final TripSentimentRepository tripSnapshotRepo;
    private final MarshalSentimentRepository marshalSnapshotRepo;

    @GetMapping("/feedbacks/{type}")
    public ResponseEntity<?> getFeedbacks(@PathVariable String type) {
        switch (type.toUpperCase()) {
            case "DRIVER":
                return ResponseEntity.ok(driverRepo.findAll());
            case "TRIP":
                return ResponseEntity.ok(tripRepo.findAll());
            case "MARSHAL":
                return ResponseEntity.ok(marshalRepo.findAll());
            case "APP":
                return ResponseEntity.ok(appRepo.findAll());
            default:
                return ResponseEntity.badRequest().body("Invalid type");
        }
    }

    @GetMapping("/details/{type}/{id}")
    public ResponseEntity<?> getEntityDetails(@PathVariable String type, @PathVariable String id) {
        String upperType = type.toUpperCase();
        switch (upperType) {
            case "DRIVER":
                List<DriverFeedback> feedbacks = driverRepo.findByDriverId(id);
                DriverSentiment snapshot = snapshotRepo.findById(id).orElse(null);
                double avg = snapshot != null ? snapshot.getAverageRating() : 0.0;
                return ResponseEntity.ok(Map.of(
                        "id", id,
                        "type", upperType,
                        "feedbacks", feedbacks,
                        "overallRating", avg,
                        "positiveCount", snapshot != null ? snapshot.getPositiveCount() : 0,
                        "neutralCount", snapshot != null ? snapshot.getNeutralCount() : 0,
                        "negativeCount", snapshot != null ? snapshot.getNegativeCount() : 0));
            case "TRIP":
                List<TripFeedback> tripFeedbacks = tripRepo.findByTripId(id);
                TripSentiment tripSnapshot = tripSnapshotRepo.findById(id).orElse(null);
                return ResponseEntity.ok(Map.of(
                        "id", id,
                        "type", upperType,
                        "feedbacks", tripFeedbacks,
                        "overallRating", tripSnapshot != null ? tripSnapshot.getAverageRating() : 0.0,
                        "positiveCount", tripSnapshot != null ? tripSnapshot.getPositiveCount() : 0,
                        "neutralCount", tripSnapshot != null ? tripSnapshot.getNeutralCount() : 0,
                        "negativeCount", tripSnapshot != null ? tripSnapshot.getNegativeCount() : 0));
            case "MARSHAL":
                List<MarshalFeedback> marshalFeedbacks = marshalRepo.findByMarshalId(id);
                MarshalSentiment marshalSnapshot = marshalSnapshotRepo.findById(id).orElse(null);
                return ResponseEntity.ok(Map.of(
                        "id", id,
                        "type", upperType,
                        "feedbacks", marshalFeedbacks,
                        "overallRating", marshalSnapshot != null ? marshalSnapshot.getAverageRating() : 0.0,
                        "positiveCount", marshalSnapshot != null ? marshalSnapshot.getPositiveCount() : 0,
                        "neutralCount", marshalSnapshot != null ? marshalSnapshot.getNeutralCount() : 0,
                        "negativeCount", marshalSnapshot != null ? marshalSnapshot.getNegativeCount() : 0));
            case "APP":
                return ResponseEntity.ok(Map.of(
                        "id", id, "type", upperType, "feedbacks", appRepo.findByUserId(id)));
            default:
                return ResponseEntity.badRequest().body("Invalid type");
        }
    }

    @GetMapping("/averages/{type}")
    public ResponseEntity<?> getAverages(@PathVariable String type) {
        String upperType = type.toUpperCase();
        switch (upperType) {
            case "DRIVER":
                return ResponseEntity.ok(snapshotRepo.findAll().stream()
                        .map(s -> Map.of("id", s.getDriverId(), "average", s.getAverageRating()))
                        .toList());
            case "APP":
                // Compute average from actual AppFeedback records
                List<AppFeedback> allAppFeedbacks = appRepo.findAll();
                double appAvg = allAppFeedbacks.stream()
                        .mapToInt(AppFeedback::getRating).average().orElse(0.0);
                return ResponseEntity
                        .ok(List.of(Map.of("id", "System", "average", appAvg, "count", allAppFeedbacks.size())));
            case "TRIP":
                return ResponseEntity.ok(tripSnapshotRepo.findAll().stream()
                        .map(s -> Map.of("id", s.getTripId(), "average", s.getAverageRating()))
                        .toList());
            case "MARSHAL":
                return ResponseEntity.ok(marshalSnapshotRepo.findAll().stream()
                        .map(s -> Map.of("id", s.getMarshalId(), "average", s.getAverageRating()))
                        .toList());
            default:
                return ResponseEntity.ok(List.of());
        }
    }
}
