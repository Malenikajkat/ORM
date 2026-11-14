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
class AssignmentServiceTest {

    @Autowired
    private AssignmentService assignmentService;

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

    private Lesson createTestLesson() {
        Category category = new Category();
        category.setName("AssignTest");
        category = categoryRepository.save(category);
        User teacher = new User();
        teacher.setName("T");
        teacher.setEmail("at@test.com");
        teacher.setRole(UserRole.TEACHER);
        teacher = userRepository.save(teacher);
        Course course = new Course();
        course.setTitle("Assign Course");
        course.setCategory(category);
        course.setTeacher(teacher);
        course = courseRepository.save(course);

        CourseModule module = new CourseModule();
        module.setTitle("Assign Module");
        module.setOrderIndex(1);
        module.setCourse(course);
        module = courseModuleRepository.save(module);

        Lesson lesson = new Lesson();
        lesson.setTitle("Assign Lesson");
        lesson.setModule(module);
        return lessonRepository.save(lesson);
    }

    @Test
    void testCreateAssignment() {
        Lesson lesson = createTestLesson();

        Assignment assignment = assignmentService.createAssignment(
                lesson.getId(), "Test Assignment", "Description",
                LocalDateTime.now().plusDays(7), 100);

        assertNotNull(assignment.getId());
        assertEquals("Test Assignment", assignment.getTitle());
        assertEquals(100, assignment.getMaxScore());
    }

    @Test
    void testFindById() {
        Lesson lesson = createTestLesson();
        Assignment assignment = assignmentService.createAssignment(
                lesson.getId(), "Find Assignment", "Desc",
                LocalDateTime.now(), 50);

        var found = assignmentService.findById(assignment.getId());
        assertTrue(found.isPresent());
        assertEquals("Find Assignment", found.get().getTitle());
    }

    @Test
    void testFindByLessonId() {
        Lesson lesson = createTestLesson();
        assignmentService.createAssignment(lesson.getId(), "A1", "D1", LocalDateTime.now(), 100);
        assignmentService.createAssignment(lesson.getId(), "A2", "D2", LocalDateTime.now(), 100);

        var assignments = assignmentService.findByLessonId(lesson.getId());
        assertEquals(2, assignments.size());
    }

    @Test
    void testDeleteAssignment() {
        Lesson lesson = createTestLesson();
        Assignment assignment = assignmentService.createAssignment(
                lesson.getId(), "Delete Me", "D", LocalDateTime.now(), 100);

        Long id = assignment.getId();
        assignmentService.deleteAssignment(id);

        assertFalse(assignmentService.findById(id).isPresent());
    }
}
