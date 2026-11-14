package org.example.orm.service;

import org.example.orm.entity.*;
import org.example.orm.exception.EntityNotFoundException;
import org.example.orm.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class QuizService {

    private static final Logger log = LoggerFactory.getLogger(QuizService.class);
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final AnswerOptionRepository answerOptionRepository;
    private final CourseModuleRepository courseModuleRepository;
    private final UserRepository userRepository;
    private final QuizSubmissionRepository quizSubmissionRepository;

    public QuizService(QuizRepository quizRepository, QuestionRepository questionRepository,
                       AnswerOptionRepository answerOptionRepository, CourseModuleRepository courseModuleRepository,
                       UserRepository userRepository, QuizSubmissionRepository quizSubmissionRepository) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.answerOptionRepository = answerOptionRepository;
        this.courseModuleRepository = courseModuleRepository;
        this.userRepository = userRepository;
        this.quizSubmissionRepository = quizSubmissionRepository;
    }

    @Transactional
    public Quiz createQuiz(Long moduleId, String title, Integer timeLimitMinutes) {
        log.info("Creating quiz: {} for module {}", title, moduleId);
        CourseModule module = courseModuleRepository.findById(moduleId)
                .orElseThrow(() -> new EntityNotFoundException("Module not found with id: " + moduleId));

        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setTimeLimitMinutes(timeLimitMinutes);
        quiz.setModule(module);
        return quizRepository.save(quiz);
    }

    @Transactional
    public Question addQuestion(Long quizId, String text, Question.QuestionType type) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found with id: " + quizId));

        Question question = new Question();
        question.setText(text);
        question.setType(type);
        question.setQuiz(quiz);
        return questionRepository.save(question);
    }

    @Transactional
    public AnswerOption addAnswerOption(Long questionId, String text, Boolean isCorrect) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("Question not found with id: " + questionId));

        AnswerOption option = new AnswerOption();
        option.setText(text);
        option.setIsCorrect(isCorrect);
        option.setQuestion(question);
        return answerOptionRepository.save(option);
    }

    @Transactional
    public QuizSubmission takeQuiz(Long quizId, Long studentId, Map<Long, Long> answers) {
        log.info("Student {} taking quiz {}", studentId, quizId);

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found with id: " + quizId));
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + studentId));

        int correctAnswers = 0;
        int totalQuestions = answers.size();

        for (Map.Entry<Long, Long> entry : answers.entrySet()) {
            Question question = questionRepository.findById(entry.getKey())
                    .orElseThrow(() -> new EntityNotFoundException("Question not found with id: " + entry.getKey()));
            AnswerOption selectedOption = answerOptionRepository.findById(entry.getValue())
                    .orElseThrow(() -> new EntityNotFoundException("Answer option not found with id: " + entry.getValue()));
            if (selectedOption.getIsCorrect()) {
                correctAnswers++;
            }
        }

        int score = totalQuestions > 0 ? (int) Math.round((double) correctAnswers / totalQuestions * 100) : 0;

        QuizSubmission submission = new QuizSubmission();
        submission.setQuiz(quiz);
        submission.setStudent(student);
        submission.setScore(score);
        submission.setTakenAt(LocalDateTime.now());
        return quizSubmissionRepository.save(submission);
    }

    @Transactional(readOnly = true)
    public Optional<Quiz> findById(Long id) {
        return quizRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Quiz> findAll() {
        return quizRepository.findAll();
    }

    @Transactional
    public Quiz updateQuiz(Long id, String title, Integer timeLimitMinutes) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found with id: " + id));
        if (title != null) {
            quiz.setTitle(title);
        }
        if (timeLimitMinutes != null) {
            quiz.setTimeLimitMinutes(timeLimitMinutes);
        }
        return quizRepository.save(quiz);
    }

    @Transactional
    public void deleteQuiz(Long id) {
        log.info("Deleting quiz with id: {}", id);
        quizRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<QuizSubmission> findByQuizId(Long quizId) {
        return quizSubmissionRepository.findByQuizId(quizId);
    }

    @Transactional(readOnly = true)
    public List<QuizSubmission> findByStudentId(Long studentId) {
        return quizSubmissionRepository.findByStudentId(studentId);
    }
}
