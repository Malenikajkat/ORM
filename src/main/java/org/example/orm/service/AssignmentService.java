package org.example.orm.service;

import org.example.orm.entity.Assignment;
import org.example.orm.entity.Lesson;
import org.example.orm.entity.Submission;
import org.example.orm.entity.User;
import org.example.orm.exception.EntityNotFoundException;
import org.example.orm.repository.AssignmentRepository;
import org.example.orm.repository.LessonRepository;
import org.example.orm.repository.SubmissionRepository;
import org.example.orm.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AssignmentService {

    private static final Logger log = LoggerFactory.getLogger(AssignmentService.class);
    private final AssignmentRepository assignmentRepository;
    private final LessonRepository lessonRepository;

    public AssignmentService(AssignmentRepository assignmentRepository, LessonRepository lessonRepository) {
        this.assignmentRepository = assignmentRepository;
        this.lessonRepository = lessonRepository;
    }

    @Transactional
    public Assignment createAssignment(Long lessonId, String title, String description,
                                       LocalDateTime dueDate, Integer maxScore) {
        log.info("Creating assignment: {} for lesson {}", title, lessonId);
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new EntityNotFoundException("Lesson not found with id: " + lessonId));

        Assignment assignment = new Assignment();
        assignment.setTitle(title);
        assignment.setDescription(description);
        assignment.setDueDate(dueDate);
        assignment.setMaxScore(maxScore);
        assignment.setLesson(lesson);
        return assignmentRepository.save(assignment);
    }

    @Transactional(readOnly = true)
    public List<Assignment> findAll() {
        return assignmentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Assignment> findById(Long id) {
        return assignmentRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Assignment> findByLessonId(Long lessonId) {
        return assignmentRepository.findByLessonId(lessonId);
    }

    @Transactional
    public Assignment updateAssignment(Long id, String title, String description,
                                       LocalDateTime dueDate, Integer maxScore) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Assignment not found with id: " + id));
        assignment.setTitle(title);
        assignment.setDescription(description);
        assignment.setDueDate(dueDate);
        assignment.setMaxScore(maxScore);
        return assignmentRepository.save(assignment);
    }

    @Transactional
    public void deleteAssignment(Long id) {
        log.info("Deleting assignment with id: {}", id);
        assignmentRepository.deleteById(id);
    }
}
