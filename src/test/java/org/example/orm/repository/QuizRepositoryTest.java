package org.example.orm.repository;

import org.example.orm.entity.*;
import org.example.orm.entity.Question.QuestionType;
import org.example.orm.entity.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class QuizRepositoryTest {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerOptionRepository answerOptionRepository;

    @Autowired
    private CourseModuleRepository courseModuleRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private QuizSubmissionRepository quizSubmissionRepository;

    private CourseModule createTestModule() {
        Category category = new Category();
        category.setName("QuizTest");
        category = categoryRepository.save(category);
        User teacher = new User();
        teacher.setName("T");
        teacher.setEmail("qt@test.com");
        teacher.setRole(UserRole.TEACHER);
        teacher = userRepository.save(teacher);
        Course course = new Course();
        course.setTitle("Quiz Test Course");
        course.setCategory(category);
        course.setTeacher(teacher);
        course = courseRepository.save(course);

        CourseModule module = new CourseModule();
        module.setTitle("Quiz Module");
        module.setOrderIndex(1);
        module.setCourse(course);
        return courseModuleRepository.save(module);
    }

    @Test
    void testCreateQuizWithQuestions() {
        CourseModule module = createTestModule();

        Quiz quiz = new Quiz();
        quiz.setTitle("Test Quiz");
        quiz.setModule(module);
        quiz = quizRepository.save(quiz);

        Question question = new Question();
        question.setText("What is 2+2?");
        question.setType(QuestionType.SINGLE_CHOICE);
        question.setQuiz(quiz);
        question = questionRepository.save(question);

        AnswerOption ao1 = new AnswerOption("@Entity", true, question);
        AnswerOption ao2 = new AnswerOption("@Table", false, question);
        answerOptionRepository.save(ao1);
        answerOptionRepository.save(ao2);

        
        quiz = quizRepository.findById(quiz.getId()).orElseThrow();
        assertEquals("Test Quiz", quiz.getTitle());
        assertEquals(1, questionRepository.findByQuizId(quiz.getId()).size());
        assertEquals(2, answerOptionRepository.findByQuestionId(question.getId()).size());
    }

    @Test
    void testQuizSubmission() {
        CourseModule module = createTestModule();

        Quiz quiz = new Quiz();
        quiz.setTitle("QS Quiz");
        quiz.setModule(module);
        quiz = quizRepository.save(quiz);

        Question question = new Question();
        question.setText("Question?");
        question.setType(QuestionType.SINGLE_CHOICE);
        question.setQuiz(quiz);
        question = questionRepository.save(question);

        answerOptionRepository.save(new AnswerOption("Correct", true, question));

        User student = new User();
        student.setName("S");
        student.setEmail("qs@test.com");
        student.setRole(UserRole.STUDENT);
        student = userRepository.save(student);

        QuizSubmission submission = new QuizSubmission();
        submission.setQuiz(quiz);
        submission.setStudent(student);
        submission.setScore(100);
        submission.setTakenAt(LocalDateTime.now());
        submission = quizSubmissionRepository.save(submission);

        var results = quizSubmissionRepository.findByStudentId(student.getId());
        assertEquals(1, results.size());
        assertEquals(100, results.get(0).getScore());
    }
}
