package org.example.orm.dto;

import jakarta.validation.constraints.NotNull;

public class SubmissionRequest {
    @NotNull
    private Long studentId;
    private String content;

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
