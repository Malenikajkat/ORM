package org.example.orm.controller;

import jakarta.validation.Valid;
import org.example.orm.dto.ReviewRequest;
import org.example.orm.entity.CourseReview;
import org.example.orm.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<CourseReview> createReview(@Valid @RequestBody ReviewRequest request) {
        CourseReview review = reviewService.createReview(
                request.getCourseId(), request.getStudentId(),
                request.getRating(), request.getComment());
        return ResponseEntity.status(HttpStatus.CREATED).body(review);
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<CourseReview>> getReviewsByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(reviewService.findByCourseId(courseId));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<CourseReview>> getReviewsByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(reviewService.findByStudentId(studentId));
    }
}
