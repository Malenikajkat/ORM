package org.example.orm.repository;

import org.example.orm.entity.*;
import org.example.orm.entity.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class SubmissionRepositoryTest {

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private CourseModuleRepository courseModuleRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Course createTestCourse() {
        Category category = new Category();
        category.setName("SubTest");
        category = categoryRepository.save(category);
        User teacher = new User();
        teacher.setName("T");
        teacher.setEmail("st@test.com");
        teacher.setRole(UserRole.TEACHER);
        teacher = userRepository.save(teacher);
        Course course = new Course();
        course.setTitle("Sub Test Course");
        course.setCategory(category);
        course.setTeacher(teacher);
        return courseRepository.save(course);
    }

    @Test
    void testCreateSubmission() {
        Course course = createTestCourse();
        CourseModule module = new CourseModule();
        module.setTitle("M");
        module.setOrderIndex(1);
        module.setCourse(course);
        module = courseModuleRepository.save(module);

        Lesson lesson = new Lesson();
        lesson.setTitle("L");
        lesson.setModule(module);
        lesson = lessonRepository.save(lesson);

        Assignment assignment = new Assignment();
        assignment.setTitle("A");
        assignment.setLesson(lesson);
        assignment.setMaxScore(100);
        assignment = assignmentRepository.save(assignment);

        User student = new User();
        student.setName("S");
        student.setEmail("sub@test.com");
        student.setRole(UserRole.STUDENT);
        student = userRepository.save(student);

        Submission submission = new Submission();
        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setContent("My answer");
        submission.setSubmittedAt(LocalDateTime.now());

        Submission saved = submissionRepository.save(submission);
        assertNotNull(saved.getId());
        assertEquals("My answer", saved.getContent());
    }

    @Test
    void testGradeSubmission() {
        Course course = createTestCourse();
        CourseModule module = new CourseModule();
        module.setTitle("M");
        module.setOrderIndex(1);
        module.setCourse(course);
        module = courseModuleRepository.save(module);

        Lesson lesson = new Lesson();
        lesson.setTitle("L");
        lesson.setModule(module);
        lesson = lessonRepository.save(lesson);

        Assignment assignment = new Assignment();
        assignment.setTitle("A");
        assignment.setLesson(lesson);
        assignment = assignmentRepository.save(assignment);

        User student = new User();
        student.setName("S");
        student.setEmail("gr@test.com");
        student.setRole(UserRole.STUDENT);
        student = userRepository.save(student);

        Submission submission = new Submission();
        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setContent("Answer");
        submission.setSubmittedAt(LocalDateTime.now());
        submission = submissionRepository.save(submission);

        submission.setScore(90);
        submission.setFeedback("Good job");
        submission = submissionRepository.save(submission);

        Submission graded = submissionRepository.findById(submission.getId()).orElseThrow();
        assertEquals(90, graded.getScore());
        assertEquals("Good job", graded.getFeedback());
    }

    @Test
    void testFindByAssignmentId() {
        Course course = createTestCourse();
        CourseModule module = new CourseModule();
        module.setTitle("M");
        module.setOrderIndex(1);
        module.setCourse(course);
        module = courseModuleRepository.save(module);

        Lesson lesson = new Lesson();
        lesson.setTitle("L");
        lesson.setModule(module);
        lesson = lessonRepository.save(lesson);

        Assignment assignment = new Assignment();
        assignment.setTitle("A");
        assignment.setLesson(lesson);
        assignment = assignmentRepository.save(assignment);

        User student = new User();
        student.setName("S");
        student.setEmail("fb@test.com");
        student.setRole(UserRole.STUDENT);
        student = userRepository.save(student);

        Submission submission = new Submission();
        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setContent("Answer");
        submission.setSubmittedAt(LocalDateTime.now());
        submissionRepository.save(submission);

        var results = submissionRepository.findByAssignmentId(assignment.getId());
        assertEquals(1, results.size());
    }
}
