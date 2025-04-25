package com.example.lab2.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendSimpleEmail(List<String> to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to.toArray(new String[0]));
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    public void sendHtmlEmail(List<String> to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false);
        helper.setTo(to.toArray(new String[0]));
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        mailSender.send(message);
    }

    public void sendEmailWithAttachment(List<String> to, String subject, String body, String pathToAttachment) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to.toArray(new String[0]));
        helper.setSubject(subject);
        helper.setText(body);
        FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
        helper.addAttachment(file.getFilename(), file);
        mailSender.send(message);
    }

//    public void sendEmail(String to, String subject, String body) {
//        SimpleMailMessage message = new SimpleMailMessage();
//
//        message.setTo(to);
//        message.setSubject(subject);
//        message.setText(body);
//
//        mailSender.send(message);
//    }

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    public Map<String, List<String>> sendEmails(List<String> emails, String subject, String body) {
        List<String> sent = new ArrayList<>();
        List<String> failed = new ArrayList<>();

        for (String email : emails) {
            try {
                sendSimpleEmail(Collections.singletonList(email), subject, body);
                sent.add(email);
            } catch (Exception e) {
                System.err.println("Не удалось отправить письмо на " + email + ": " + e.getMessage());
                failed.add(email);
            }
        }

        Map<String, List<String>> result = new HashMap<>();
        result.put("sended", sent);
        result.put("dont send", failed);
        return result;
    }



}

