package org.example.orm.service;

import org.example.orm.entity.Category;
import org.example.orm.entity.Course;
import org.example.orm.entity.Enrollment;
import org.example.orm.entity.User;
import org.example.orm.entity.enums.EnrollmentStatus;
import org.example.orm.entity.enums.UserRole;
import org.example.orm.repository.CategoryRepository;
import org.example.orm.repository.CourseRepository;
import org.example.orm.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class EnrollmentServiceTest {

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Test
    void testEnrollStudent() {
        Category category = new Category();
        category.setName("EnrollSvc");
        category = categoryRepository.save(category);
        User teacher = new User();
        teacher.setName("T");
        teacher.setEmail("es@test.com");
        teacher.setRole(UserRole.TEACHER);
        teacher = userRepository.save(teacher);
        Course course = new Course();
        course.setTitle("Enroll Course");
        course.setCategory(category);
        course.setTeacher(teacher);
        course = courseRepository.save(course);

        User student = new User();
        student.setName("Student");
        student.setEmail("enroll@test.com");
        student.setRole(UserRole.STUDENT);
        student = userRepository.save(student);

        Enrollment enrollment = enrollmentService.enrollStudent(course.getId(), student.getId());
        assertNotNull(enrollment.getId());
        assertEquals(EnrollmentStatus.ACTIVE, enrollment.getStatus());
    }

    @Test
    void testDuplicateEnrollment() {
        Category category = new Category();
        category.setName("DupSvc");
        category = categoryRepository.save(category);
        User teacher = new User();
        teacher.setName("T");
        teacher.setEmail("ds@test.com");
        teacher.setRole(UserRole.TEACHER);
        teacher = userRepository.save(teacher);
        Course course = new Course();
        course.setTitle("Dup Course");
        course.setCategory(category);
        course.setTeacher(teacher);
        course = courseRepository.save(course);

        User student = new User();
        student.setName("S");
        student.setEmail("dup@test.com");
        student.setRole(UserRole.STUDENT);
        student = userRepository.save(student);

        Long courseId = course.getId();
        Long studentId = student.getId();
        enrollmentService.enrollStudent(courseId, studentId);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                enrollmentService.enrollStudent(courseId, studentId));
        assertTrue(ex.getMessage().contains("already enrolled"));
    }

    @Test
    void testFindEnrollmentsByStudent() {
        Category category = new Category();
        category.setName("FindEn");
        category = categoryRepository.save(category);
        User teacher = new User();
        teacher.setName("T");
        teacher.setEmail("fe@test.com");
        teacher.setRole(UserRole.TEACHER);
        teacher = userRepository.save(teacher);
        Course course = new Course();
        course.setTitle("Find Enroll Course");
        course.setCategory(category);
        course.setTeacher(teacher);
        course = courseRepository.save(course);

        User student = new User();
        student.setName("S");
        student.setEmail("finden@test.com");
        student.setRole(UserRole.STUDENT);
        student = userRepository.save(student);

        enrollmentService.enrollStudent(course.getId(), student.getId());

        List<Enrollment> enrollments = enrollmentService.findByStudentId(student.getId());
        assertEquals(1, enrollments.size());
    }
}
