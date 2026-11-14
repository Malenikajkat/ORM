package org.example.orm.service;

import org.example.orm.entity.*;
import org.example.orm.entity.Question.QuestionType;
import org.example.orm.entity.enums.UserRole;
import org.example.orm.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class QuizServiceTest {

    @Autowired
    private QuizService quizService;

    @Autowired
    private CourseModuleRepository courseModuleRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerOptionRepository answerOptionRepository;

    @Autowired
    private QuizRepository quizRepository;

    private CourseModule createTestModule() {
        Category category = new Category();
        category.setName("QuizSvcTest");
        category = categoryRepository.save(category);
        User teacher = new User();
        teacher.setName("T");
        teacher.setEmail("qs@test.com");
        teacher.setRole(UserRole.TEACHER);
        teacher = userRepository.save(teacher);
        Course course = new Course();
        course.setTitle("QuizSvc Course");
        course.setCategory(category);
        course.setTeacher(teacher);
        course = courseRepository.save(course);

        CourseModule module = new CourseModule();
        module.setTitle("QuizSvc Module");
        module.setOrderIndex(1);
        module.setCourse(course);
        return courseModuleRepository.save(module);
    }

    @Test
    void testCreateQuiz() {
        CourseModule module = createTestModule();

        var quiz = quizService.createQuiz(module.getId(), "Test Quiz", 30);
        assertNotNull(quiz.getId());
        assertEquals("Test Quiz", quiz.getTitle());
        assertEquals(30, quiz.getTimeLimitMinutes());
    }

    @Test
    void testAddQuestion() {
        CourseModule module = createTestModule();
        var quiz = quizService.createQuiz(module.getId(), "Question Quiz", 30);

        var question = quizService.addQuestion(quiz.getId(), "What is 2+2?", QuestionType.SINGLE_CHOICE);
        assertNotNull(question.getId());
        assertEquals("What is 2+2?", question.getText());
    }

    @Test
    void testAddAnswerOption() {
        CourseModule module = createTestModule();
        var quiz = quizService.createQuiz(module.getId(), "Option Quiz", 30);
        var question = quizService.addQuestion(quiz.getId(), "Question?", QuestionType.SINGLE_CHOICE);

        var option = quizService.addAnswerOption(question.getId(), "Correct Answer", true);
        assertNotNull(option.getId());
        assertTrue(option.getIsCorrect());
    }

    @Test
    void testTakeQuiz() {
        CourseModule module = createTestModule();
        var quiz = quizService.createQuiz(module.getId(), "Take Quiz", 30);
        var question = quizService.addQuestion(quiz.getId(), "What is JPA?", QuestionType.SINGLE_CHOICE);
        var correctOption = quizService.addAnswerOption(question.getId(), "ORM Library", true);
        quizService.addAnswerOption(question.getId(), "Database", false);
        quizService.addAnswerOption(question.getId(), "Language", false);

        User student = new User();
        student.setName("S");
        student.setEmail("tq@test.com");
        student.setRole(UserRole.STUDENT);
        student = userRepository.save(student);

        var submission = quizService.takeQuiz(quiz.getId(), student.getId(), Map.of(question.getId(), correctOption.getId()));
        assertNotNull(submission.getId());
        assertEquals(100, submission.getScore());
    }

    @Test
    void testTakeQuizWrongAnswer() {
        CourseModule module = createTestModule();
        var quiz = quizService.createQuiz(module.getId(), "Wrong Quiz", 30);
        var question = quizService.addQuestion(quiz.getId(), "Question?", QuestionType.SINGLE_CHOICE);
        var correctOption = quizService.addAnswerOption(question.getId(), "Correct", true);
        var wrongOption = quizService.addAnswerOption(question.getId(), "Wrong", false);

        User student = new User();
        student.setName("S");
        student.setEmail("twq@test.com");
        student.setRole(UserRole.STUDENT);
        student = userRepository.save(student);

        var submission = quizService.takeQuiz(quiz.getId(), student.getId(), Map.of(question.getId(), wrongOption.getId()));
        assertEquals(0, submission.getScore());
    }

    @Test
    void testFindByQuizId() {
        CourseModule module = createTestModule();
        var quiz = quizService.createQuiz(module.getId(), "Find Quiz", 30);

        User student = new User();
        student.setName("S");
        student.setEmail("fq@test.com");
        student.setRole(UserRole.STUDENT);
        student = userRepository.save(student);

        var question = quizService.addQuestion(quiz.getId(), "Q?", QuestionType.SINGLE_CHOICE);
        var option = quizService.addAnswerOption(question.getId(), "A", true);
        quizService.takeQuiz(quiz.getId(), student.getId(), Map.of(question.getId(), option.getId()));

        var submissions = quizService.findByQuizId(quiz.getId());
        assertEquals(1, submissions.size());
    }

    @Test
    void testFindByStudentId() {
        CourseModule module = createTestModule();
        var quiz = quizService.createQuiz(module.getId(), "Student Quiz", 30);

        User student = new User();
        student.setName("S");
        student.setEmail("fsq@test.com");
        student.setRole(UserRole.STUDENT);
        student = userRepository.save(student);

        var question = quizService.addQuestion(quiz.getId(), "Q?", QuestionType.SINGLE_CHOICE);
        var option = quizService.addAnswerOption(question.getId(), "A", true);
        quizService.takeQuiz(quiz.getId(), student.getId(), Map.of(question.getId(), option.getId()));

        var submissions = quizService.findByStudentId(student.getId());
        assertEquals(1, submissions.size());
    }
}
