package org.example.orm.service;

import org.example.orm.entity.*;
import org.example.orm.exception.EntityNotFoundException;
import org.example.orm.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CourseService {

    private static final Logger log = LoggerFactory.getLogger(CourseService.class);

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final CourseModuleRepository courseModuleRepository;
    private final LessonRepository lessonRepository;
    private final TagRepository tagRepository;

    public CourseService(CourseRepository courseRepository, CategoryRepository categoryRepository,
                         UserRepository userRepository, CourseModuleRepository courseModuleRepository,
                         LessonRepository lessonRepository, TagRepository tagRepository) {
        this.courseRepository = courseRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.courseModuleRepository = courseModuleRepository;
        this.lessonRepository = lessonRepository;
        this.tagRepository = tagRepository;
    }

    @Transactional
    public Course createCourse(String title, String description, Long categoryId, Long teacherId,
                               Set<Long> tagIds, LocalDate duration, LocalDate startDate) {
        log.info("Creating course: {}", title);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + categoryId));
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new EntityNotFoundException("Teacher not found with id: " + teacherId));

        Set<Tag> tags = tagIds != null ?
                tagRepository.findAllById(tagIds).stream().collect(java.util.stream.Collectors.toSet()) :
                Set.of();

        Course course = new Course();
        course.setTitle(title);
        course.setDescription(description);
        course.setCategory(category);
        course.setTeacher(teacher);
        course.setDuration(duration);
        course.setStartDate(startDate != null ? startDate : LocalDate.now());
        course.setTags(tags);

        return courseRepository.save(course);
    }

    @Transactional
    public CourseModule addModule(Long courseId, String title, String description, Integer orderIndex) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + courseId));

        CourseModule module = new CourseModule();
        module.setTitle(title);
        module.setDescription(description);
        module.setCourse(course);
        module.setOrderIndex(orderIndex);

        course.getModules().add(module);
        return courseModuleRepository.save(module);
    }

    @Transactional
    public Lesson addLesson(Long moduleId, String title, String content, String videoUrl) {
        CourseModule module = courseModuleRepository.findById(moduleId)
                .orElseThrow(() -> new EntityNotFoundException("Module not found with id: " + moduleId));

        Lesson lesson = new Lesson();
        lesson.setTitle(title);
        lesson.setContent(content);
        lesson.setVideoUrl(videoUrl);
        lesson.setModule(module);

        module.getLessons().add(lesson);
        return lessonRepository.save(lesson);
    }

    @Transactional(readOnly = true)
    public Optional<Course> findById(Long id) {
        return courseRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Course> findByIdWithStructure(Long id) {
        return courseRepository.findByIdWithFullStructure(id);
    }

    @Transactional(readOnly = true)
    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Course> findByCategoryId(Long categoryId) {
        return courseRepository.findByCategoryId(categoryId);
    }

    @Transactional(readOnly = true)
    public List<Course> findByTeacherId(Long teacherId) {
        return courseRepository.findByTeacherId(teacherId);
    }

    @Transactional(readOnly = true)
    public List<Course> searchByTitle(String title) {
        return courseRepository.findByTitleContainingIgnoreCase(title);
    }

    @Transactional
    public Course updateCourse(Long id, String title, String description) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + id));
        course.setTitle(title);
        course.setDescription(description);
        return courseRepository.save(course);
    }

    @Transactional
    public void deleteCourse(Long id) {
        log.info("Deleting course with id: {}", id);
        courseRepository.deleteById(id);
    }
}
