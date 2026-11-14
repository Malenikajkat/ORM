package org.example.orm.service;

import org.example.orm.entity.*;
import org.example.orm.entity.enums.UserRole;
import org.example.orm.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class SubmissionServiceTest {

    @Autowired
    private SubmissionService submissionService;

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

    private Assignment createTestAssignment() {
        Category category = new Category();
        category.setName("SubSvcTest");
        category = categoryRepository.save(category);
        User teacher = new User();
        teacher.setName("T");
        teacher.setEmail("ss@test.com");
        teacher.setRole(UserRole.TEACHER);
        teacher = userRepository.save(teacher);
        Course course = new Course();
        course.setTitle("SubSvc Course");
        course.setCategory(category);
        course.setTeacher(teacher);
        course = courseRepository.save(course);

        CourseModule module = new CourseModule();
        module.setTitle("SubSvc Module");
        module.setOrderIndex(1);
        module.setCourse(course);
        module = courseModuleRepository.save(module);

        Lesson lesson = new Lesson();
        lesson.setTitle("SubSvc Lesson");
        lesson.setModule(module);
        lesson = lessonRepository.save(lesson);

        Assignment assignment = new Assignment();
        assignment.setTitle("SubSvc Assignment");
        assignment.setDescription("Test");
        assignment.setMaxScore(100);
        assignment.setLesson(lesson);
        return assignmentRepository.save(assignment);
    }

    @Test
    void testSubmitAssignment() {
        Assignment assignment = createTestAssignment();

        User student = new User();
        student.setName("S");
        student.setEmail("subsvc@test.com");
        student.setRole(UserRole.STUDENT);
        student = userRepository.save(student);

        var submission = submissionService.submitAssignment(assignment.getId(), student.getId(), "My answer");
        assertNotNull(submission.getId());
        assertEquals("My answer", submission.getContent());
    }

    @Test
    void testGradeSubmission() {
        Assignment assignment = createTestAssignment();

        User student = new User();
        student.setName("S");
        student.setEmail("grsvc@test.com");
        student.setRole(UserRole.STUDENT);
        student = userRepository.save(student);

        var submission = submissionService.submitAssignment(assignment.getId(), student.getId(), "Answer");
        submissionService.gradeSubmission(submission.getId(), 95, "Great work!");

        var graded = submissionService.findById(submission.getId());
        assertTrue(graded.isPresent());
        assertEquals(95, graded.get().getScore());
        assertEquals("Great work!", graded.get().getFeedback());
    }

    @Test
    void testDuplicateSubmission() {
        Assignment assignment = createTestAssignment();

        User student = new User();
        student.setName("S");
        student.setEmail("dupsvc@test.com");
        student.setRole(UserRole.STUDENT);
        student = userRepository.save(student);

        submissionService.submitAssignment(assignment.getId(), student.getId(), "First");

        final Long aId = assignment.getId();
        final Long sId = student.getId();
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                submissionService.submitAssignment(aId, sId, "Second"));
        assertTrue(ex.getMessage().contains("already submitted"));
    }

    @Test
    void testFindByAssignmentId() {
        Assignment assignment = createTestAssignment();

        User student1 = new User();
        student1.setName("S1");
        student1.setEmail("fa1@test.com");
        student1.setRole(UserRole.STUDENT);
        student1 = userRepository.save(student1);

        User student2 = new User();
        student2.setName("S2");
        student2.setEmail("fa2@test.com");
        student2.setRole(UserRole.STUDENT);
        student2 = userRepository.save(student2);

        submissionService.submitAssignment(assignment.getId(), student1.getId(), "A1");
        submissionService.submitAssignment(assignment.getId(), student2.getId(), "A2");

        var submissions = submissionService.findByAssignmentId(assignment.getId());
        assertEquals(2, submissions.size());
    }

    @Test
    void testFindByStudentId() {
        Assignment assignment = createTestAssignment();

        User student = new User();
        student.setName("S");
        student.setEmail("fsvc@test.com");
        student.setRole(UserRole.STUDENT);
        student = userRepository.save(student);

        submissionService.submitAssignment(assignment.getId(), student.getId(), "A1");

        var submissions = submissionService.findByStudentId(student.getId());
        assertEquals(1, submissions.size());
    }
}
