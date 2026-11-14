package org.example.orm.service;

import org.example.orm.entity.*;
import org.example.orm.entity.enums.UserRole;
import org.example.orm.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ReviewServiceTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Course createTestCourse() {
        Category category = new Category();
        category.setName("ReviewSvcTest");
        category = categoryRepository.save(category);
        User teacher = new User();
        teacher.setName("T");
        teacher.setEmail("rs@test.com");
        teacher.setRole(UserRole.TEACHER);
        teacher = userRepository.save(teacher);
        Course course = new Course();
        course.setTitle("ReviewSvc Course");
        course.setCategory(category);
        course.setTeacher(teacher);
        return courseRepository.save(course);
    }

    @Test
    void testCreateReview() {
        Course course = createTestCourse();

        User student = new User();
        student.setName("S");
        student.setEmail("revsvc@test.com");
        student.setRole(UserRole.STUDENT);
        student = userRepository.save(student);

        var review = reviewService.createReview(course.getId(), student.getId(), 5, "Excellent!");
        assertNotNull(review.getId());
        assertEquals(5, review.getRating());
        assertEquals("Excellent!", review.getComment());
    }

    @Test
    void testFindByCourseId() {
        Course course = createTestCourse();

        User student1 = new User();
        student1.setName("S1");
        student1.setEmail("fc1@test.com");
        student1.setRole(UserRole.STUDENT);
        student1 = userRepository.save(student1);

        User student2 = new User();
        student2.setName("S2");
        student2.setEmail("fc2@test.com");
        student2.setRole(UserRole.STUDENT);
        student2 = userRepository.save(student2);

        reviewService.createReview(course.getId(), student1.getId(), 5, "Great");
        reviewService.createReview(course.getId(), student2.getId(), 4, "Good");

        var reviews = reviewService.findByCourseId(course.getId());
        assertEquals(2, reviews.size());
    }

    @Test
    void testFindByStudentId() {
        Course course = createTestCourse();

        User student = new User();
        student.setName("S");
        student.setEmail("fsvc@test.com");
        student.setRole(UserRole.STUDENT);
        student = userRepository.save(student);

        reviewService.createReview(course.getId(), student.getId(), 5, "Great");

        var reviews = reviewService.findByStudentId(student.getId());
        assertEquals(1, reviews.size());
    }
}
