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
class CourseRepositoryTest {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testCreateAndFindCourse() {
        Category category = new Category();
        category.setName("Programming");
        category = categoryRepository.save(category);

        User teacher = new User();
        teacher.setName("Teacher");
        teacher.setEmail("t@test.com");
        teacher.setRole(UserRole.TEACHER);
        teacher = userRepository.save(teacher);

        Course course = new Course();
        course.setTitle("Hibernate Course");
        course.setDescription("Learn Hibernate");
        course.setCategory(category);
        course.setTeacher(teacher);

        Course saved = courseRepository.save(course);
        assertNotNull(saved.getId());

        var found = courseRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Hibernate Course", found.get().getTitle());
    }

    @Test
    void testCascadePersistModules() {
        Category category = new Category();
        category.setName("Design");
        category = categoryRepository.save(category);
        User teacher = new User();
        teacher.setName("Teacher2");
        teacher.setEmail("t2@test.com");
        teacher.setRole(UserRole.TEACHER);
        teacher = userRepository.save(teacher);

        Course course = new Course();
        course.setTitle("Design Course");
        course.setCategory(category);
        course.setTeacher(teacher);

        CourseModule module = new CourseModule();
        module.setTitle("Module 1");
        module.setOrderIndex(1);
        module.setCourse(course);
        course.getModules().add(module);

        Lesson lesson = new Lesson();
        lesson.setTitle("Lesson 1");
        lesson.setModule(module);
        module.getLessons().add(lesson);

        Course saved = courseRepository.save(course);

        var found = courseRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(1, found.get().getModules().size());
        assertEquals(1, found.get().getModules().get(0).getLessons().size());
    }

    @Test
    void testDeleteCourse() {
        Category category = new Category();
        category.setName("Test");
        category = categoryRepository.save(category);
        User teacher = new User();
        teacher.setName("T");
        teacher.setEmail("del@test.com");
        teacher.setRole(UserRole.TEACHER);
        teacher = userRepository.save(teacher);

        Course course = new Course();
        course.setTitle("Delete Me");
        course.setCategory(category);
        course.setTeacher(teacher);

        Course saved = courseRepository.save(course);
        Long id = saved.getId();

        courseRepository.deleteById(id);
        assertFalse(courseRepository.findById(id).isPresent());
    }
}
