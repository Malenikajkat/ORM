package org.example.orm.repository;

import org.example.orm.entity.*;
import org.example.orm.entity.enums.EnrollmentStatus;
import org.example.orm.entity.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class EnrollmentRepositoryTest {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void testCreateEnrollment() {
        Category category = new Category();
        category.setName("Test");
        category = categoryRepository.save(category);
        User teacher = new User();
        teacher.setName("T");
        teacher.setEmail("te@test.com");
        teacher.setRole(UserRole.TEACHER);
        teacher = userRepository.save(teacher);
        Course course = new Course();
        course.setTitle("Test Course");
        course.setCategory(category);
        course.setTeacher(teacher);
        course = courseRepository.save(course);

        User student = new User();
        student.setName("Student");
        student.setEmail("s@test.com");
        student.setRole(UserRole.STUDENT);
        student = userRepository.save(student);

        Enrollment enrollment = new Enrollment();
        enrollment.setCourse(course);
        enrollment.setStudent(student);
        enrollment.setStatus(EnrollmentStatus.ACTIVE);
        enrollment.setEnrolledAt(LocalDateTime.now());

        Enrollment saved = enrollmentRepository.save(enrollment);
        assertNotNull(saved.getId());
    }

    @Test
    void testUniqueConstraint() {
        Category category = new Category();
        category.setName("Unique");
        category = categoryRepository.save(category);
        User teacher = new User();
        teacher.setName("T");
        teacher.setEmail("tu@test.com");
        teacher.setRole(UserRole.TEACHER);
        teacher = userRepository.save(teacher);
        Course course = new Course();
        course.setTitle("Unique Course");
        course.setCategory(category);
        course.setTeacher(teacher);
        course = courseRepository.save(course);

        User student = new User();
        student.setName("S");
        student.setEmail("su@test.com");
        student.setRole(UserRole.STUDENT);
        student = userRepository.save(student);

        assertFalse(enrollmentRepository.existsByCourseIdAndStudentId(course.getId(), student.getId()));

        Enrollment enrollment = new Enrollment();
        enrollment.setCourse(course);
        enrollment.setStudent(student);
        enrollment.setStatus(EnrollmentStatus.ACTIVE);
        enrollment.setEnrolledAt(LocalDateTime.now());
        enrollmentRepository.save(enrollment);

        assertTrue(enrollmentRepository.existsByCourseIdAndStudentId(course.getId(), student.getId()));

        
        Enrollment duplicate = new Enrollment();
        duplicate.setCourse(course);
        duplicate.setStudent(student);
        duplicate.setStatus(EnrollmentStatus.ACTIVE);
        duplicate.setEnrolledAt(LocalDateTime.now());
        org.junit.jupiter.api.Assertions.assertThrows(Exception.class, () ->
                enrollmentRepository.save(duplicate));
    }

    @Test
    void testFindByStudentId() {
        Category category = new Category();
        category.setName("Find");
        category = categoryRepository.save(category);
        User teacher = new User();
        teacher.setName("T");
        teacher.setEmail("tf@test.com");
        teacher.setRole(UserRole.TEACHER);
        teacher = userRepository.save(teacher);
        Course course = new Course();
        course.setTitle("Find Course");
        course.setCategory(category);
        course.setTeacher(teacher);
        course = courseRepository.save(course);

        User student = new User();
        student.setName("S");
        student.setEmail("sf@test.com");
        student.setRole(UserRole.STUDENT);
        student = userRepository.save(student);

        Enrollment enrollment = new Enrollment();
        enrollment.setCourse(course);
        enrollment.setStudent(student);
        enrollment.setStatus(EnrollmentStatus.ACTIVE);
        enrollment.setEnrolledAt(LocalDateTime.now());
        enrollmentRepository.save(enrollment);

        var results = enrollmentRepository.findByStudentId(student.getId());
        assertEquals(1, results.size());
        assertEquals(student.getId(), results.get(0).getStudent().getId());
    }
}
