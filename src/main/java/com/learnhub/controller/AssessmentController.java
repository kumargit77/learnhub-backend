package com.learnhub.controller;

import com.learnhub.dto.AssessmentSubmissionRequest;
import com.learnhub.model.*;
import com.learnhub.repository.AssessmentAttemptRepository;
import com.learnhub.repository.EnrollmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/assessments")
public class AssessmentController {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private AssessmentAttemptRepository attemptRepository;

    @GetMapping("/attempts/{enrollmentId}")
    public List<AssessmentAttempt> getAttempts(@PathVariable UUID enrollmentId) {
        return attemptRepository.findByEnrollmentId(enrollmentId);
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submitAssessment(@RequestBody AssessmentSubmissionRequest request) {
        Optional<Enrollment> enrollmentOpt = enrollmentRepository.findByStudentIdAndCourseId(request.getStudentId(), request.getCourseId());
        
        if (enrollmentOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Enrollment not found");
        }
        
        Enrollment enrollment = enrollmentOpt.get();
        Course course = enrollment.getCourse();
        List<Question> questions = course.getQuestions();

        int correctCount = 0;
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            String ans = request.getAnswers().get(i);
            if (q.getCorrectOption().equals(ans)) {
                correctCount++;
            }
        }
        
        double percentage = questions.isEmpty() ? 0 : ((double) correctCount / questions.size()) * 100;
        boolean passed = percentage >= 60.0;

        List<AssessmentAttempt> attempts = attemptRepository.findByEnrollmentId(enrollment.getId());
        
        AssessmentAttempt attempt = new AssessmentAttempt();
        attempt.setEnrollment(enrollment);
        attempt.setAttemptNumber(attempts.size() + 1);
        attempt.setScorePercentage(new BigDecimal(percentage).setScale(2, RoundingMode.HALF_UP));
        attempt.setPassed(passed);
        
        AssessmentAttempt saved = attemptRepository.save(attempt);
        
        if (passed || attempts.size() >= 2) {
             enrollment.setStatus("COMPLETED");
             enrollment.setCompletedAt(LocalDateTime.now());
             enrollmentRepository.save(enrollment);
        }

        return ResponseEntity.ok(saved);
    }
}
