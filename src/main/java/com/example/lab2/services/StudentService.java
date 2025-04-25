package com.example.lab2.services;

import com.example.lab2.entities.Student;
import com.example.lab2.repository.StudentRepository;
import com.example.lab2.dto.PagedResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;



import com.example.lab2.specification.StudentSpecification;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class StudentService {
    @Autowired
    private StudentRepository studentRepository;




    public Page<Student> getStudents(Pageable pageable) {
        return studentRepository.findAll(pageable);
    }



    public PagedResponse<Student> getFilteredStudents(
            String firstName,
            String lastName,
            String firstNameLike,
            String lastNameLike,
            String email,
            String group,
            int page,
            int size,
            Sort sort
    ) {
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Student> spec = Specification.where(null);

        if (firstName != null) {
            spec = spec.and(StudentSpecification.hasFirstName(firstName));
        }
        if (firstNameLike != null) {
            spec = spec.and(StudentSpecification.hasFirstNameLike(firstNameLike));
        }
        if (lastName != null) {
            spec = spec.and(StudentSpecification.hasLastName(lastName));
        }
        if (lastNameLike != null) {
            spec = spec.and(StudentSpecification.hasLastNameLike(lastNameLike));
        }
        if (email != null) {
            spec = spec.and(StudentSpecification.hasEmail(email));
        }
        if (group != null) {
            spec = spec.and(StudentSpecification.hasGroup(group));
        }

        Page<Student> studentPage = studentRepository.findAll(spec, pageable);

        return new PagedResponse<>(
                studentPage.getContent(),
                studentPage.getNumber(),
                studentPage.getSize(),
                studentPage.getTotalElements(),
                studentPage.getTotalPages(),
                studentPage.isLast()
        );
    }

    public List<String> getValidStudentEmails(List<String> emails) {
        return emails.stream()
                .filter(email -> studentRepository.existsByEmail(email))
                .collect(Collectors.toList());
    }


}