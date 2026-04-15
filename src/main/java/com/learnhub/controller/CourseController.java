package com.learnhub.controller;

import com.learnhub.dto.CourseRequestDTO;
import com.learnhub.model.Course;
import com.learnhub.model.Question;
import com.learnhub.model.Section;
import com.learnhub.model.User;
import com.learnhub.repository.CourseRepository;
import com.learnhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable UUID id) {
        return courseRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Course> createCourse(@RequestBody CourseRequestDTO request) {
        Course course = new Course();
        course.setName(request.getName());
        course.setDescription(request.getDesc());
        course.setDurationMins(request.getDuration());

        if(request.getCreatedBy() != null) {
            userRepository.findById(request.getCreatedBy()).ifPresent(course::setCreatedBy);
        }

        if (request.getSections() != null) {
            int order = 1;
            for (CourseRequestDTO.SectionDTO sdto : request.getSections()) {
                Section section = new Section();
                section.setName(sdto.getName());
                section.setPptUrl(sdto.getFileName());
                section.setDurationMins(sdto.getDurationMins() != null ? sdto.getDurationMins() : 5);
                section.setOrderIndex(order++);
                section.setCourse(course);
                course.getSections().add(section);
            }
        }

        if (request.getQuestions() != null) {
            for (CourseRequestDTO.QuestionDTO qdto : request.getQuestions()) {
                Question question = new Question();
                question.setQuestionText(qdto.getText());
                question.setOptionA(qdto.getA());
                question.setOptionB(qdto.getB());
                question.setOptionC(qdto.getC());
                question.setOptionD(qdto.getD());
                question.setCorrectOption(qdto.getCorrect());
                question.setCourse(course);
                course.getQuestions().add(question);
            }
        }

        Course savedCourse = courseRepository.save(course);
        return ResponseEntity.ok(savedCourse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourse(@PathVariable UUID id, @RequestBody CourseRequestDTO request) {
        return courseRepository.findById(id).map(course -> {
            course.setName(request.getName());
            course.setDescription(request.getDesc());
            course.setDurationMins(request.getDuration());

            course.getSections().clear();
            course.getQuestions().clear();
            courseRepository.flush(); // Force DELETE statements before new INSERTs

            if (request.getSections() != null) {
                int order = 1;
                for (CourseRequestDTO.SectionDTO sdto : request.getSections()) {
                    Section section = new Section();
                    section.setName(sdto.getName());
                    section.setPptUrl(sdto.getFileName());
                    section.setDurationMins(sdto.getDurationMins() != null ? sdto.getDurationMins() : 5);
                    section.setOrderIndex(order++);
                    section.setCourse(course);
                    course.getSections().add(section);
                }
            }

            if (request.getQuestions() != null) {
                for (CourseRequestDTO.QuestionDTO qdto : request.getQuestions()) {
                    Question question = new Question();
                    question.setQuestionText(qdto.getText());
                    question.setOptionA(qdto.getA());
                    question.setOptionB(qdto.getB());
                    question.setOptionC(qdto.getC());
                    question.setOptionD(qdto.getD());
                    question.setCorrectOption(qdto.getCorrect());
                    question.setCourse(course);
                    course.getQuestions().add(question);
                }
            }

            Course savedCourse = courseRepository.save(course);
            return ResponseEntity.ok(savedCourse);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable UUID id) {
        if (!courseRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        courseRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
