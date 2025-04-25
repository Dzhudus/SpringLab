package com.example.lab2.controller;

import com.example.lab2.entities.Course;
import com.example.lab2.entities.Enrollment;
import com.example.lab2.entities.Student;
import com.example.lab2.repository.EnrollmentRepository;
import com.example.lab2.repository.StudentRepository;
import com.example.lab2.repository.CourseRepository;
import com.example.lab2.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;



@RestController
@RequestMapping("/enrollments")
@CrossOrigin(origins = "*")
public class EnrollmentController {
    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    @Autowired
    private EmailService emailService;

    public EnrollmentController(EnrollmentRepository enrollmentRepository, StudentRepository studentRepository, CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    @GetMapping
    public Page<Enrollment> getAllEnrollments(Pageable pageable) {
        return enrollmentRepository.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Enrollment> getEnrollmentById(@PathVariable Long id) {
        return enrollmentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/student/{studentId}")
    public List<Enrollment> getEnrollmentsByStudent(@PathVariable Long studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }

    @GetMapping("/course/{courseId}")
    public List<Enrollment> getEnrollmentsByCourse(@PathVariable Long courseId) {
        return enrollmentRepository.findByCourseId(courseId);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createEnrollment(@RequestBody Enrollment enrollment) {
        Optional<Student> studentOpt = studentRepository.findById(enrollment.getStudent().getId());
        Optional<Course> courseOpt = courseRepository.findById(enrollment.getCourse().getId());

        if (studentOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Student not found");
        }
        if (courseOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Course not found");
        }

        Enrollment newEnrollment = new Enrollment();
        newEnrollment.setStudent(studentOpt.get());
        newEnrollment.setCourse(courseOpt.get());
        newEnrollment.setEnrollmentDate(enrollment.getEnrollmentDate());

        Enrollment savedEnrollment = enrollmentRepository.save(newEnrollment);




            String subject = "Enrollment Confirmation";
            String body = String.format("Dear %s %s,\n\nYou have been successfully enrolled in the course \"%s\" starting from %s.\n\nBest regards,",
                    studentOpt.get().getFirstName(),
                    studentOpt.get().getLastName(),
                    courseOpt.get().getName(),
                    enrollment.getEnrollmentDate().toString());

            emailService.sendEmail(studentOpt.get().getEmail(), subject, body);


        return ResponseEntity.ok(savedEnrollment);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Enrollment> updateEnrollment(@PathVariable Long id, @RequestBody Enrollment enrollmentDetails) {
        return enrollmentRepository.findById(id)
                .map(enrollment -> {
                    enrollment.setStudent(enrollmentDetails.getStudent());
                    enrollment.setCourse(enrollmentDetails.getCourse());
                    enrollment.setEnrollmentDate(enrollmentDetails.getEnrollmentDate());
                    return ResponseEntity.ok(enrollmentRepository.save(enrollment));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnrollment(@PathVariable Long id) {
        if (enrollmentRepository.existsById(id)) {
            enrollmentRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
