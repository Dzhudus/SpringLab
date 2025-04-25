package com.example.lab2.controller;

import com.example.lab2.dto.MassEmailRequest;
import com.example.lab2.services.EmailService;
import com.example.lab2.services.StudentService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class EmailController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private StudentService studentService;

    @PostMapping("/send-simple-email")
    public String sendSimpleEmail(@RequestBody Map<String, Object> request) {
        List<String> to = (List<String>) request.get("to");
        String subject = (String) request.get("subject");
        String body = (String) request.get("body");

        List<String> validEmails = studentService.getValidStudentEmails(to);

        if (validEmails.isEmpty()) {
            return "Ни один email не найден среди студентов";
        }
        emailService.sendSimpleEmail(to, subject, body);
        return "Simple email sent.";
    }

    @PostMapping("/send-html-email")
    public String sendHtmlEmail(@RequestBody Map<String, Object> request) throws MessagingException {
        List<String> to = (List<String>) request.get("to");
        String subject = (String) request.get("subject");
        String body = (String) request.get("body");

        List<String> validEmails = studentService.getValidStudentEmails(to);

        if (validEmails.isEmpty()) {
            return "Ни один email не найден среди студентов";
        }

        emailService.sendHtmlEmail(to, subject, body);
        return "HTML email sent.";
    }

    @PostMapping("/send-email-with-attachment")
    public String sendEmailWithAttachment(@RequestBody Map<String, Object> request) throws MessagingException {
        List<String> to = (List<String>) request.get("to");
        String subject = (String) request.get("subject");
        String body = (String) request.get("body");
        String path = (String) request.get("path");


        List<String> validEmails = studentService.getValidStudentEmails(to);

        if (validEmails.isEmpty()) {
            return "Ни один email не найден среди студентов";
        }
        emailService.sendEmailWithAttachment(to, subject, body, path);
        return "Email with attachment sent.";
    }


    @PostMapping("/send-to-students")
    public ResponseEntity<?> sendToStudents(@RequestBody MassEmailRequest request) {
        List<String> allEmails = request.getEmails();
        List<String> validEmails = studentService.getValidStudentEmails(allEmails);

//        List<String> invalidEmails = new ArrayList<>(allEmails);
//        invalidEmails.removeAll(validEmails);
//
//        if (validEmails.isEmpty()) {
//            return ResponseEntity.badRequest().body("Ни один email не найден среди студентов.");
//        }

        emailService.sendEmails(validEmails, request.getSubject(), request.getBody());

        Map<String, Object> response = new HashMap<>();
        response.put("sent", validEmails);
//        response.put("notFound", invalidEmails);

        return ResponseEntity.ok(response);
    }

}