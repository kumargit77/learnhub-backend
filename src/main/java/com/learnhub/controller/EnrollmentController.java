package com.learnhub.controller;

import com.learnhub.dto.EnrollmentRequest;
import com.learnhub.model.*;
import com.learnhub.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    @Autowired
    private EnrollmentRepository enrollmentRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SectionProgressRepository sectionProgressRepository;

    @GetMapping("/all")
    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    @GetMapping("/student/{studentId}")
    public List<Enrollment> getStudentEnrollments(@PathVariable UUID studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }
    
    @PostMapping
    public ResponseEntity<?> enrollCourse(@RequestBody EnrollmentRequest request) {
        Optional<User> student = userRepository.findById(request.getStudentId());
        Optional<Course> course = courseRepository.findById(request.getCourseId());

        if (student.isEmpty() || course.isEmpty()) {
            return ResponseEntity.badRequest().body("Student or Course not found");
        }

        Optional<Enrollment> existing = enrollmentRepository.findByStudentIdAndCourseId(request.getStudentId(), request.getCourseId());
        if (existing.isPresent()) {
            return ResponseEntity.badRequest().body("Already enrolled");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student.get());
        enrollment.setCourse(course.get());
        enrollment.setStatus("ENROLLED");
        
        Enrollment saved = enrollmentRepository.save(enrollment);
        
        // Initialize section progress
        for(Section section : course.get().getSections()) {
            SectionProgress sp = new SectionProgress();
            sp.setEnrollment(saved);
            sp.setSection(section);
            sp.setCompleted(false);
            sectionProgressRepository.save(sp);
        }

        return ResponseEntity.ok(saved);
    }

    @PostMapping("/{enrollmentId}/sections/{sectionId}/complete")
    public ResponseEntity<?> completeSection(@PathVariable UUID enrollmentId, @PathVariable UUID sectionId) {
        Optional<SectionProgress> spOpt = sectionProgressRepository.findByEnrollmentIdAndSectionId(enrollmentId, sectionId);
        if (spOpt.isEmpty()) return ResponseEntity.badRequest().body("Progress not found");
        
        SectionProgress sp = spOpt.get();
        sp.setCompleted(true);
        sectionProgressRepository.save(sp);

        Enrollment enrollment = sp.getEnrollment();
        if ("ENROLLED".equals(enrollment.getStatus())) {
            enrollment.setStatus("IN_PROGRESS");
            enrollmentRepository.save(enrollment);
        }

        return ResponseEntity.ok(sp);
    }

    @GetMapping("/{enrollmentId}/progress")
    public List<SectionProgress> getEnrollmentProgress(@PathVariable UUID enrollmentId) {
        return sectionProgressRepository.findByEnrollmentId(enrollmentId);
    }
}
