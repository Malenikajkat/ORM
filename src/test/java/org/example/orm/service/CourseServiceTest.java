package org.example.orm.service;

import org.example.orm.entity.Category;
import org.example.orm.entity.Course;
import org.example.orm.entity.User;
import org.example.orm.entity.enums.UserRole;
import org.example.orm.repository.CategoryRepository;
import org.example.orm.repository.CourseRepository;
import org.example.orm.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CourseServiceTest {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Test
    void testCreateCourse() {
        Category category = new Category();
        category.setName("CourseSvc");
        category = categoryRepository.save(category);

        User teacher = new User();
        teacher.setName("Teacher");
        teacher.setEmail("cs@test.com");
        teacher.setRole(UserRole.TEACHER);
        teacher = userRepository.save(teacher);

        Course course = courseService.createCourse(
                "Spring Boot Course", "Learn Spring Boot",
                category.getId(), teacher.getId(),
                null, null, null
        );

        assertNotNull(course.getId());
        assertEquals("Spring Boot Course", course.getTitle());
    }

    @Test
    void testFindById() {
        Category category = new Category();
        category.setName("FindSvc");
        category = categoryRepository.save(category);
        User teacher = new User();
        teacher.setName("T");
        teacher.setEmail("fs@test.com");
        teacher.setRole(UserRole.TEACHER);
        teacher = userRepository.save(teacher);

        Course course = new Course();
        course.setTitle("Find Course");
        course.setCategory(category);
        course.setTeacher(teacher);
        course = courseRepository.save(course);

        var found = courseService.findById(course.getId());
        assertTrue(found.isPresent());
        assertEquals("Find Course", found.get().getTitle());
    }

    @Test
    void testCreateAndDeleteCourse() {
        Category category = new Category();
        category.setName("DelSvc");
        category = categoryRepository.save(category);
        User teacher = new User();
        teacher.setName("T");
        teacher.setEmail("ds@test.com");
        teacher.setRole(UserRole.TEACHER);
        teacher = userRepository.save(teacher);

        Course course = courseService.createCourse(
                "Delete Me", "Desc",
                category.getId(), teacher.getId(),
                null, null, null
        );

        Long id = course.getId();
        courseService.deleteCourse(id);

        assertFalse(courseService.findById(id).isPresent());
    }
}
