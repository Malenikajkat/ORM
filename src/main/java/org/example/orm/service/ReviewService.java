package org.example.orm.service;

import org.example.orm.entity.Course;
import org.example.orm.entity.CourseReview;
import org.example.orm.entity.User;
import org.example.orm.exception.EntityNotFoundException;
import org.example.orm.repository.CourseRepository;
import org.example.orm.repository.CourseReviewRepository;
import org.example.orm.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewService {

    private static final Logger log = LoggerFactory.getLogger(ReviewService.class);
    private final CourseReviewRepository reviewRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public ReviewService(CourseReviewRepository reviewRepository,
                         CourseRepository courseRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CourseReview createReview(Long courseId, Long studentId, Integer rating, String comment) {
        log.info("Creating review for course {} by student {}", courseId, studentId);
        if (reviewRepository.existsByCourseIdAndStudentId(courseId, studentId)) {
            throw new RuntimeException("Student already submitted this assignment");
        }
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + courseId));
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + studentId));

        CourseReview review = new CourseReview();
        review.setCourse(course);
        review.setStudent(student);
        review.setRating(rating);
        review.setComment(comment);
        review.setCreatedAt(LocalDateTime.now());
        return reviewRepository.save(review);
    }

    @Transactional(readOnly = true)
    public List<CourseReview> findByCourseId(Long courseId) {
        return reviewRepository.findByCourseId(courseId);
    }

    @Transactional(readOnly = true)
    public List<CourseReview> findByStudentId(Long studentId) {
        return reviewRepository.findByStudentId(studentId);
    }
}
