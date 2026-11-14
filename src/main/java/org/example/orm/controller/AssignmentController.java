package org.example.orm.controller;

import jakarta.validation.Valid;
import org.example.orm.dto.AssignmentRequest;
import org.example.orm.dto.GradeSubmissionRequest;
import org.example.orm.dto.SubmissionRequest;
import org.example.orm.entity.Assignment;
import org.example.orm.entity.Submission;
import org.example.orm.service.AssignmentService;
import org.example.orm.service.SubmissionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final SubmissionService submissionService;

    public AssignmentController(AssignmentService assignmentService, SubmissionService submissionService) {
        this.assignmentService = assignmentService;
        this.submissionService = submissionService;
    }

    @PostMapping
    public ResponseEntity<Assignment> createAssignment(@Valid @RequestBody AssignmentRequest request) {
        Assignment assignment = assignmentService.createAssignment(
                request.getLessonId(), request.getTitle(), request.getDescription(),
                request.getDueDate(), request.getMaxScore());
        return ResponseEntity.status(HttpStatus.CREATED).body(assignment);
    }

    @GetMapping
    public ResponseEntity<List<Assignment>> getAllAssignments() {
        return ResponseEntity.ok(assignmentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Assignment> getAssignment(@PathVariable Long id) {
        return assignmentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Assignment> updateAssignment(@PathVariable Long id,
                                                        @Valid @RequestBody AssignmentRequest request) {
        Assignment assignment = assignmentService.updateAssignment(
                id, request.getTitle(), request.getDescription(),
                request.getDueDate(), request.getMaxScore());
        return ResponseEntity.ok(assignment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssignment(@PathVariable Long id) {
        assignmentService.deleteAssignment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/lessons/{lessonId}")
    public ResponseEntity<List<Assignment>> getAssignmentsByLesson(@PathVariable Long lessonId) {
        return ResponseEntity.ok(assignmentService.findByLessonId(lessonId));
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<Submission> submitAssignment(@PathVariable Long id,
                                                        @Valid @RequestBody SubmissionRequest request) {
        Submission submission = submissionService.submitAssignment(id, request.getStudentId(), request.getContent());
        return ResponseEntity.status(HttpStatus.CREATED).body(submission);
    }

    @GetMapping("/submissions/{submissionId}")
    public ResponseEntity<Submission> getSubmission(@PathVariable Long submissionId) {
        return submissionService.findById(submissionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/submissions/{submissionId}/grade")
    public ResponseEntity<Submission> gradeSubmission(@PathVariable Long submissionId,
                                                       @Valid @RequestBody GradeSubmissionRequest request) {
        Submission submission = submissionService.gradeSubmission(submissionId, request.getScore(), request.getFeedback());
        return ResponseEntity.ok(submission);
    }

    @GetMapping("/{id}/submissions")
    public ResponseEntity<List<Submission>> getSubmissions(@PathVariable Long id) {
        return ResponseEntity.ok(submissionService.findByAssignmentId(id));
    }

    @GetMapping("/students/{studentId}")
    public ResponseEntity<List<Submission>> getStudentSubmissions(@PathVariable Long studentId) {
        return ResponseEntity.ok(submissionService.findByStudentId(studentId));
    }
}
