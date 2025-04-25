package com.example.lab2.services;

import com.example.lab2.entities.Course;
import com.example.lab2.entities.Enrollment;
import com.example.lab2.entities.Student;
import com.example.lab2.repository.EnrollmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;

@Service
public class EnrollmentService {
    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private EmailService emailService;

    public Enrollment enrollStudent(Student student, Course course) {
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrollmentDate(LocalDate.now());

        enrollmentRepository.save(enrollment);

        String subject = "Enrollment Confirmation";
        String body = String.format("Dear %s %s,\n\nYou have been successfully enrolled in the course \"%s\" starting from %s.\n\nBest regards,",
                student.getFirstName(),
                student.getLastName(),
                course.getName(),
                enrollment.getEnrollmentDate().toString());

        emailService.sendEmail(student.getEmail(), subject, body);

        return enrollment;
    }
}
