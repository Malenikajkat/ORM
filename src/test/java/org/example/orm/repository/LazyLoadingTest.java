package org.example.orm.repository;

import org.example.orm.entity.*;
import org.example.orm.entity.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class LazyLoadingTest {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseModuleRepository courseModuleRepository;

    @Autowired
    private LessonRepository lessonRepository;

    
    @Test
    void testLazyInitializationExceptionOnModules() {
        
        Category category = new Category();
        category.setName("LazyTest");
        category = categoryRepository.save(category);
        User teacher = new User();
        teacher.setName("T");
        teacher.setEmail("lazy@test.com");
        teacher.setRole(UserRole.TEACHER);
        teacher = userRepository.save(teacher);

        Course course = new Course();
        course.setTitle("Lazy Course");
        course.setCategory(category);
        course.setTeacher(teacher);
        course = courseRepository.save(course);

        CourseModule module = new CourseModule();
        module.setTitle("Lazy Module");
        module.setOrderIndex(1);
        module.setCourse(course);
        module = courseModuleRepository.save(module);

        Lesson lesson = new Lesson();
        lesson.setTitle("Lazy Lesson");
        lesson.setModule(module);
        lesson = lessonRepository.save(lesson);

        
        Course loadedCourse = courseRepository.findById(course.getId()).orElseThrow();

        
        
        assertThrows(org.hibernate.LazyInitializationException.class, () -> {
            
            loadedCourse.getModules().size();
        });
    }

    
    @Test
    void testEagerLoadingWithJoinFetch() {
        Category category = new Category();
        category.setName("EagerTest");
        category = categoryRepository.save(category);
        User teacher = new User();
        teacher.setName("T");
        teacher.setEmail("eager@test.com");
        teacher.setRole(UserRole.TEACHER);
        teacher = userRepository.save(teacher);

        Course course = new Course();
        course.setTitle("Eager Course");
        course.setCategory(category);
        course.setTeacher(teacher);
        course = courseRepository.save(course);

        CourseModule module = new CourseModule();
        module.setTitle("Eager Module");
        module.setOrderIndex(1);
        module.setCourse(course);
        module = courseModuleRepository.save(module);

        Lesson lesson = new Lesson();
        lesson.setTitle("Eager Lesson");
        lesson.setModule(module);
        lesson = lessonRepository.save(lesson);

        
        Course loadedCourse = courseRepository.findByIdWithFullStructure(course.getId()).orElseThrow();

        
        assertEquals(1, loadedCourse.getModules().size());
        
        Long moduleId = loadedCourse.getModules().get(0).getId();
        assertEquals(1, lessonRepository.findByModuleId(moduleId).size());
    }
}
