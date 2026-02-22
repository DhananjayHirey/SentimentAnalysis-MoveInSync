package com.moveinsync.feedbackService.controller;

import com.moveinsync.feedbackService.dto.FeedbackRequest;
import com.moveinsync.feedbackService.entity.*;
import com.moveinsync.feedbackService.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final DriverFeedbackRepository driverRepository;
    private final TripFeedbackRepository tripRepository;
    private final MarshalFeedbackRepository marshalRepository;
    private final AppFeedbackRepository appRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @PostMapping
    public ResponseEntity<?> submit(@RequestBody FeedbackRequest request) {
        String type = request.getEntityType().toUpperCase();
        String id = request.getEntityId();
        int rating = request.getRating();
        String comment = request.getComment();
        LocalDateTime now = LocalDateTime.now();

        switch (type) {
            case "DRIVER":
                DriverFeedback driverFeedback = DriverFeedback.builder()
                        .driverId(id).rating(rating).comment(comment).createdAt(now).build();
                driverRepository.save(driverFeedback);
                kafkaTemplate.send("feedback-events", id, driverFeedback);
                break;
            case "TRIP":
                tripRepository.save(TripFeedback.builder()
                        .tripId(id).rating(rating).comment(comment).createdAt(now).build());
                break;
            case "MARSHAL":
                marshalRepository.save(MarshalFeedback.builder()
                        .marshalId(id).rating(rating).comment(comment).createdAt(now).build());
                break;
            case "APP":
                appRepository.save(AppFeedback.builder()
                        .userId(id).rating(rating).comment(comment).createdAt(now).build());
                break;
            default:
                return ResponseEntity.badRequest().body("Invalid entity type");
        }

        return ResponseEntity.ok("Feedback submitted successfully");
    }

    @GetMapping("/drivers/ratings")
    public ResponseEntity<?> getAllDriverRatings() {
        List<Object[]> results = driverRepository.findAllDriversAverageRatings();
        return ResponseEntity.ok(results.stream().map(r -> Map.of(
                "driverId", r[0],
                "averageRating", r[1])).collect(Collectors.toList()));
    }

    @GetMapping("/admin/feedbacks/{type}")
    public ResponseEntity<?> getFeedbacks(@PathVariable String type) {
        switch (type.toUpperCase()) {
            case "DRIVER":
                return ResponseEntity.ok(driverRepository.findAll());
            case "TRIP":
                return ResponseEntity.ok(tripRepository.findAll());
            case "MARSHAL":
                return ResponseEntity.ok(marshalRepository.findAll());
            case "APP":
                return ResponseEntity.ok(appRepository.findAll());
            default:
                return ResponseEntity.badRequest().body("Invalid type");
        }
    }

    @GetMapping("/admin/averages/{type}")
    public ResponseEntity<?> getAverages(@PathVariable String type) {
        switch (type.toUpperCase()) {
            case "DRIVER":
                return ResponseEntity.ok(driverRepository.findAllDriversAverageRatings().stream()
                        .map(r -> Map.of("id", r[0], "average", r[1])).collect(Collectors.toList()));
            case "TRIP":
                return ResponseEntity.ok(tripRepository.findAllTripsAverageRatings().stream()
                        .map(r -> Map.of("id", r[0], "average", r[1])).collect(Collectors.toList()));
            case "MARSHAL":
                return ResponseEntity.ok(marshalRepository.findAllMarshalsAverageRatings().stream()
                        .map(r -> Map.of("id", r[0], "average", r[1])).collect(Collectors.toList()));
            case "APP":
                return ResponseEntity.ok(Map.of("average", appRepository.findAverageRating()));
            default:
                return ResponseEntity.badRequest().body("Invalid type");
        }
    }

    @GetMapping("/admin/details/{type}/{id}")
    public ResponseEntity<?> getEntityDetails(@PathVariable String type, @PathVariable String id) {
        switch (type.toUpperCase()) {
            case "DRIVER":
                List<DriverFeedback> df = driverRepository.findByDriverId(id);
                Double dAvg = driverRepository.findAverageRatingByDriverId(id);
                return ResponseEntity.ok(Map.of("id", id, "type", "DRIVER", "feedbacks", df, "overallRating",
                        dAvg != null ? dAvg : 0.0));
            case "TRIP":
                List<TripFeedback> tf = tripRepository.findByTripId(id);
                Double tAvg = tripRepository.findAverageRatingByTripId(id);
                return ResponseEntity.ok(
                        Map.of("id", id, "type", "TRIP", "feedbacks", tf, "overallRating", tAvg != null ? tAvg : 0.0));
            case "MARSHAL":
                List<MarshalFeedback> mf = marshalRepository.findByMarshalId(id);
                Double mAvg = marshalRepository.findAverageRatingByMarshalId(id);
                return ResponseEntity.ok(Map.of("id", id, "type", "MARSHAL", "feedbacks", mf, "overallRating",
                        mAvg != null ? mAvg : 0.0));
            case "APP":
                List<AppFeedback> af = appRepository.findByUserId(id);
                return ResponseEntity.ok(Map.of("id", id, "type", "APP", "feedbacks", af));
            default:
                return ResponseEntity.badRequest().body("Invalid type");
        }
    }
}
