package com.example.lab2.controller;


import com.example.lab2.entities.Enrollment;
import com.example.lab2.entities.Student;
import com.example.lab2.entities.Course;
import com.example.lab2.repository.EnrollmentRepository;
import com.example.lab2.repository.StudentRepository;
import com.example.lab2.repository.CourseRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teachers")
@PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
public class TeacherController {
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;

    public TeacherController(StudentRepository studentRepository,
                             EnrollmentRepository enrollmentRepository,
                             CourseRepository courseRepository) {
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
    }

    @GetMapping("/students")
    public ResponseEntity<List<Student>> getAllStudents() {
        List<Student> students = studentRepository.findAll();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/enrollments")
    public ResponseEntity<List<Enrollment>> getAllEnrollments() {
        List<Enrollment> enrollments = enrollmentRepository.findAll();
        return ResponseEntity.ok(enrollments);
    }


    @PostMapping("/students")
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        Student savedStudent = studentRepository.save(student);
        return ResponseEntity.ok(savedStudent);
    }

    @PostMapping("/enrollments")
    public ResponseEntity<?> enrollStudent(@RequestParam Long studentId, @RequestParam Long courseId) {
        Student student = studentRepository.findById(studentId).orElse(null);
        Course course = courseRepository.findById(courseId).orElse(null);

        if (student == null || course == null) {
            return ResponseEntity.badRequest().body("Студент или курс не найден");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollmentRepository.save(enrollment);

        return ResponseEntity.ok("Студент успешно записан на курс");
    }
}
