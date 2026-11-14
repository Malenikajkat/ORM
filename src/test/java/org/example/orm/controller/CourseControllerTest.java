package org.example.orm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.orm.entity.Category;
import org.example.orm.entity.Course;
import org.example.orm.entity.User;
import org.example.orm.entity.enums.UserRole;
import org.example.orm.repository.CategoryRepository;
import org.example.orm.repository.CourseRepository;
import org.example.orm.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateCourse() throws Exception {
        Category category = new Category();
        category.setName("ApiTest");
        category = categoryRepository.save(category);
        User teacher = new User();
        teacher.setName("T");
        teacher.setEmail("api@test.com");
        teacher.setRole(UserRole.TEACHER);
        teacher = userRepository.save(teacher);

        String requestBody = """
                {
                    "title": "REST API Course",
                    "description": "Testing REST API",
                    "categoryId": %d,
                    "teacherId": %d
                }
                """.formatted(category.getId(), teacher.getId());

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("REST API Course"))
                .andExpect(jsonPath("$.description").value("Testing REST API"));
    }

    @Test
    void testGetCourse() throws Exception {
        Category category = new Category();
        category.setName("GetApi");
        category = categoryRepository.save(category);
        User teacher = new User();
        teacher.setName("T");
        teacher.setEmail("ga@test.com");
        teacher.setRole(UserRole.TEACHER);
        teacher = userRepository.save(teacher);

        Course course = new Course();
        course.setTitle("Get Course");
        course.setCategory(category);
        course.setTeacher(teacher);
        course = courseRepository.save(course);

        mockMvc.perform(get("/api/courses/" + course.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Get Course"));
    }

    @Test
    void testGetCourseNotFound() throws Exception {
        mockMvc.perform(get("/api/courses/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllCourses() throws Exception {
        Category category = new Category();
        category.setName("AllApi");
        category = categoryRepository.save(category);
        User teacher = new User();
        teacher.setName("T");
        teacher.setEmail("aa@test.com");
        teacher.setRole(UserRole.TEACHER);
        teacher = userRepository.save(teacher);

        Course course = new Course();
        course.setTitle("All Course");
        course.setCategory(category);
        course.setTeacher(teacher);
        courseRepository.save(course);

        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }
}
