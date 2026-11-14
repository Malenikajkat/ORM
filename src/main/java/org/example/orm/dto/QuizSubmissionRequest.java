package org.example.orm.dto;

import jakarta.validation.constraints.NotNull;

import java.util.Map;

public class QuizSubmissionRequest {
    @NotNull
    private Long quizId;
    @NotNull
    private Long studentId;
    @NotNull
    private Map<Long, Long> answers;

    public Long getQuizId() { return quizId; }
    public void setQuizId(Long quizId) { this.quizId = quizId; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public Map<Long, Long> getAnswers() { return answers; }
    public void setAnswers(Map<Long, Long> answers) { this.answers = answers; }
}
