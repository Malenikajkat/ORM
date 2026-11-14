package org.example.orm.controller;

import jakarta.validation.Valid;
import org.example.orm.dto.QuizRequest;
import org.example.orm.dto.QuizSubmissionRequest;
import org.example.orm.entity.Question;
import org.example.orm.entity.Quiz;
import org.example.orm.entity.QuizSubmission;
import org.example.orm.entity.AnswerOption;
import org.example.orm.service.QuizService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping
    public ResponseEntity<Quiz> createQuiz(@Valid @RequestBody QuizRequest request) {
        Quiz quiz = quizService.createQuiz(
                request.getModuleId(), request.getTitle(), request.getTimeLimitMinutes());
        return ResponseEntity.status(HttpStatus.CREATED).body(quiz);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Quiz> updateQuiz(@PathVariable Long id,
                                           @Valid @RequestBody QuizRequest request) {
        Quiz quiz = quizService.updateQuiz(id, request.getTitle(), request.getTimeLimitMinutes());
        return ResponseEntity.ok(quiz);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long id) {
        quizService.deleteQuiz(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Quiz>> getAllQuizzes() {
        return ResponseEntity.ok(quizService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Quiz> getQuiz(@PathVariable Long id) {
        return quizService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/take")
    public ResponseEntity<QuizSubmission> takeQuiz(@PathVariable Long id,
                                                    @Valid @RequestBody QuizSubmissionRequest request) {
        QuizSubmission submission = quizService.takeQuiz(
                request.getQuizId(), request.getStudentId(), request.getAnswers());
        return ResponseEntity.status(HttpStatus.CREATED).body(submission);
    }

    @GetMapping("/{id}/submissions")
    public ResponseEntity<List<QuizSubmission>> getQuizSubmissions(@PathVariable Long id) {
        return ResponseEntity.ok(quizService.findByQuizId(id));
    }

    @GetMapping("/student/{studentId}/submissions")
    public ResponseEntity<List<QuizSubmission>> getStudentQuizSubmissions(@PathVariable Long studentId) {
        return ResponseEntity.ok(quizService.findByStudentId(studentId));
    }

    @PostMapping("/{quizId}/questions")
    public ResponseEntity<Question> addQuestion(@PathVariable Long quizId,
                                                 @RequestParam String text,
                                                 @RequestParam String type) {
        Question question = quizService.addQuestion(quizId, text, Question.QuestionType.valueOf(type));
        return ResponseEntity.status(HttpStatus.CREATED).body(question);
    }

    @PostMapping("/questions/{questionId}/options")
    public ResponseEntity<AnswerOption> addAnswerOption(@PathVariable Long questionId,
                                                         @RequestParam String text,
                                                         @RequestParam Boolean isCorrect) {
        AnswerOption option = quizService.addAnswerOption(questionId, text, isCorrect);
        return ResponseEntity.status(HttpStatus.CREATED).body(option);
    }
}
