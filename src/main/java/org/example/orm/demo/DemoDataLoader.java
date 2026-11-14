package org.example.orm.demo;

import org.example.orm.entity.*;
import org.example.orm.entity.enums.EnrollmentStatus;
import org.example.orm.entity.enums.UserRole;
import org.example.orm.entity.Question.QuestionType;
import org.example.orm.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Component
@org.springframework.context.annotation.Profile("dev")
public class DemoDataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoDataLoader.class);

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final CourseRepository courseRepository;
    private final CourseModuleRepository courseModuleRepository;
    private final LessonRepository lessonRepository;
    private final AssignmentRepository assignmentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final SubmissionRepository submissionRepository;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final AnswerOptionRepository answerOptionRepository;
    private final QuizSubmissionRepository quizSubmissionRepository;
    private final CourseReviewRepository reviewRepository;
    private final ProfileRepository profileRepository;

    public DemoDataLoader(UserRepository userRepository, CategoryRepository categoryRepository,
                          TagRepository tagRepository, CourseRepository courseRepository,
                          CourseModuleRepository courseModuleRepository, LessonRepository lessonRepository,
                          AssignmentRepository assignmentRepository, EnrollmentRepository enrollmentRepository,
                          SubmissionRepository submissionRepository, QuizRepository quizRepository,
                          QuestionRepository questionRepository, AnswerOptionRepository answerOptionRepository,
                          QuizSubmissionRepository quizSubmissionRepository, CourseReviewRepository reviewRepository,
                          ProfileRepository profileRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.courseRepository = courseRepository;
        this.courseModuleRepository = courseModuleRepository;
        this.lessonRepository = lessonRepository;
        this.assignmentRepository = assignmentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.submissionRepository = submissionRepository;
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.answerOptionRepository = answerOptionRepository;
        this.quizSubmissionRepository = quizSubmissionRepository;
        this.reviewRepository = reviewRepository;
        this.profileRepository = profileRepository;
    }

    @Override
    public void run(String... args) {
        if (categoryRepository.count() > 0) {
            log.info("Demo data already exists, skipping...");
            return;
        }
        log.info("Loading demo data...");

        Category programming = new Category();
        programming.setName("Programming");
        programming = categoryRepository.save(programming);

        Category design = new Category();
        design.setName("Design");
        design = categoryRepository.save(design);

        Tag javaTag = new Tag();
        javaTag.setName("Java");
        javaTag = tagRepository.save(javaTag);

        Tag hibernateTag = new Tag();
        hibernateTag.setName("Hibernate");
        hibernateTag = tagRepository.save(hibernateTag);

        Tag springTag = new Tag();
        springTag.setName("Spring");
        springTag = tagRepository.save(springTag);

        Tag sqlTag = new Tag();
        sqlTag.setName("SQL");
        sqlTag = tagRepository.save(sqlTag);

        User teacher = new User();
        teacher.setName("Dr. Ivan Petrov");
        teacher.setEmail("teacher@example.com");
        teacher.setRole(UserRole.TEACHER);
        teacher = userRepository.save(teacher);
        userRepository.flush();
        Profile teacherProfile = new Profile();
        teacherProfile.setUser(teacher);
        teacherProfile.setBio("Senior developer");
        teacher.setProfile(teacherProfile);
        teacherProfile = profileRepository.save(teacherProfile);

        User student1 = new User();
        student1.setName("Alexey Smirnov");
        student1.setEmail("student1@example.com");
        student1.setRole(UserRole.STUDENT);
        student1 = userRepository.save(student1);
        userRepository.flush();
        Profile student1Profile = new Profile();
        student1Profile.setUser(student1);
        student1Profile.setBio("Junior developer");
        student1.setProfile(student1Profile);
        student1Profile = profileRepository.save(student1Profile);

        User student2 = new User();
        student2.setName("Maria Kozlova");
        student2.setEmail("student2@example.com");
        student2.setRole(UserRole.STUDENT);
        student2 = userRepository.save(student2);
        userRepository.flush();
        Profile student2Profile = new Profile();
        student2Profile.setUser(student2);
        student2Profile.setBio("CS student");
        student2.setProfile(student2Profile);
        student2Profile = profileRepository.save(student2Profile);

        Course course = new Course();
        course.setTitle("ORM and Hibernate Fundamentals");
        course.setDescription("Learn ORM, Hibernate, and JPA from scratch");
        course.setStartDate(LocalDate.of(2026, 11, 1));
        course.setCategory(programming);
        course.setTeacher(teacher);
        course.setTags(Set.of(javaTag, hibernateTag, sqlTag));
        course = courseRepository.save(course);

        CourseModule module1 = new CourseModule();
        module1.setTitle("Introduction to ORM");
        module1.setDescription("Basic concepts");
        module1.setOrderIndex(1);
        module1.setCourse(course);
        module1 = courseModuleRepository.save(module1);

        CourseModule module2 = new CourseModule();
        module2.setTitle("JPA Annotations");
        module2.setDescription("Deep dive");
        module2.setOrderIndex(2);
        module2.setCourse(course);
        module2 = courseModuleRepository.save(module2);

        Lesson lesson1 = new Lesson();
        lesson1.setTitle("What is ORM?");
        lesson1.setContent("ORM bridges the gap...");
        lesson1.setModule(module1);
        lesson1 = lessonRepository.save(lesson1);

        Lesson lesson2 = new Lesson();
        lesson2.setTitle("Entity Mapping");
        lesson2.setContent("@Entity, @Table...");
        lesson2.setModule(module1);
        lesson2 = lessonRepository.save(lesson2);

        Assignment assignment1 = new Assignment();
        assignment1.setTitle("First Entity");
        assignment1.setDescription("Create a simple JPA entity");
        assignment1.setDueDate(LocalDateTime.of(2026, 11, 20, 23, 59));
        assignment1.setMaxScore(100);
        assignment1.setLesson(lesson1);
        assignment1 = assignmentRepository.save(assignment1);

        Assignment assignment2 = new Assignment();
        assignment2.setTitle("Relationships");
        assignment2.setDescription("Implement OneToMany");
        assignment2.setDueDate(LocalDateTime.of(2026, 11, 25, 23, 59));
        assignment2.setMaxScore(100);
        assignment2.setLesson(lesson2);
        assignment2 = assignmentRepository.save(assignment2);

        Enrollment e1 = new Enrollment();
        e1.setCourse(course); e1.setStudent(student1);
        e1.setStatus(EnrollmentStatus.ACTIVE);
        e1.setEnrolledAt(LocalDateTime.of(2026, 11, 1, 10, 0));
        enrollmentRepository.save(e1);

        Enrollment e2 = new Enrollment();
        e2.setCourse(course); e2.setStudent(student2);
        e2.setStatus(EnrollmentStatus.ACTIVE);
        e2.setEnrolledAt(LocalDateTime.of(2026, 11, 1, 11, 0));
        enrollmentRepository.save(e2);

        Submission s1 = new Submission();
        s1.setAssignment(assignment1); s1.setStudent(student1);
        s1.setContent("package com.example; @Entity public class User {}");
        s1.setScore(95); s1.setFeedback("Great work!");
        s1.setSubmittedAt(LocalDateTime.of(2026, 11, 18, 15, 30));
        submissionRepository.save(s1);

        Submission s2 = new Submission();
        s2.setAssignment(assignment1); s2.setStudent(student2);
        s2.setContent("package com.example; @Entity public class User {}");
        s2.setScore(88); s2.setFeedback("Good");
        s2.setSubmittedAt(LocalDateTime.of(2026, 11, 19, 9, 0));
        submissionRepository.save(s2);

        Quiz quiz = new Quiz();
        quiz.setTitle("JPA Annotations Quiz");
        quiz.setTimeLimitMinutes(30);
        quiz.setModule(module2);
        quiz = quizRepository.save(quiz);

        Question q1 = new Question();
        q1.setText("Which annotation marks a class as a JPA entity?");
        q1.setType(QuestionType.SINGLE_CHOICE);
        q1.setQuiz(quiz);
        q1 = questionRepository.save(q1);
        AnswerOption ao1 = new AnswerOption("@Entity", true, q1);
        AnswerOption ao2 = new AnswerOption("@Table", false, q1);
        AnswerOption ao3 = new AnswerOption("@Column", false, q1);
        answerOptionRepository.save(ao1);
        answerOptionRepository.save(ao2);
        answerOptionRepository.save(ao3);

        Question q2 = new Question();
        q2.setText("Which fetch type is default for @OneToMany?");
        q2.setType(QuestionType.SINGLE_CHOICE);
        q2.setQuiz(quiz);
        q2 = questionRepository.save(q2);
        AnswerOption ao4 = new AnswerOption("EAGER", false, q2);
        AnswerOption ao5 = new AnswerOption("LAZY", true, q2);
        answerOptionRepository.save(ao4);
        answerOptionRepository.save(ao5);

        QuizSubmission qs = new QuizSubmission();
        qs.setQuiz(quiz); qs.setStudent(student1);
        qs.setScore(100); qs.setTakenAt(LocalDateTime.of(2026, 11, 15, 14, 0));
        quiz.getQuizSubmissions().add(qs);
        quizSubmissionRepository.save(qs);

        CourseReview cr = new CourseReview();
        cr.setCourse(course); cr.setStudent(student1);
        cr.setRating(5); cr.setComment("Excellent course!");
        cr.setCreatedAt(LocalDateTime.of(2026, 11, 20, 18, 0));
        reviewRepository.save(cr);

        log.info("Demo data loaded successfully!");
    }
}
