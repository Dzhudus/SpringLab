package com.example.lab2.controller;

import com.example.lab2.entities.Enrollment;
import com.example.lab2.entities.Student;
import com.example.lab2.repository.EnrollmentRepository;
import com.example.lab2.repository.StudentRepository;
import com.example.lab2.dto.PagedResponse;
import com.example.lab2.services.StudentService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/students")
@PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
public class StudentController {

    private final StudentService studentService;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;

    public StudentController(StudentService studentService,
                             StudentRepository studentRepository,
                             EnrollmentRepository enrollmentRepository) {
        this.studentService = studentService;
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
    }



    @GetMapping
    public ResponseEntity<List<Student>> getStudentsByParams(@RequestParam Map<String, String> params) {
        String firstName = params.get("firstName");
        String lastName = params.get("lastName");
        String email = params.get("email");
        String groupName = params.get("groupName");

        // если ни одного валидного параметра нет — вернуть 400
        if (firstName == null && lastName == null && email == null && groupName == null) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }

        List<Student> result = studentRepository.findAll().stream()
                .filter(s -> firstName == null || s.getFirstName().equalsIgnoreCase(firstName))
                .filter(s -> lastName == null || s.getLastName().equalsIgnoreCase(lastName))
                .filter(s -> email == null || s.getEmail().equalsIgnoreCase(email))
                .filter(s -> groupName == null || s.getGroupName().equalsIgnoreCase(groupName))
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // 🔹 Фильтрация студентов
    @GetMapping("/filter")
    public PagedResponse<Student> getFilteredStudents(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false, name = "name_like") String nameLike,
            @RequestParam(required = false, name = "lastName_like") String lastNameLike,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String group,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id,desc") String[] sort
    ) {
        Sort.Direction direction = sort.length > 1 && sort[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sortObj = Sort.by(direction, sort[0]);

        return studentService.getFilteredStudents(
                firstName, lastName, nameLike, lastNameLike, email, group, page, size, sortObj
        );
    }



    @GetMapping("/all")
    public ResponseEntity<List<Student>> getAllStudents() {
        List<Student> students = studentRepository.findAll();
        return ResponseEntity.ok(students);
    }
    // 🔹 Создание студента
    @PostMapping("/create")
    public ResponseEntity<?> createStudent(@RequestBody Student student) {
        if (studentRepository.existsByEmail(student.getEmail())) {
            return ResponseEntity.badRequest().body("Студент с таким email уже существует");
        }

        Student savedStudent = studentRepository.save(student);
        return ResponseEntity.ok(savedStudent);
    }

    // 🔹 Получить свои записи
    @GetMapping("/enrollments")
    public ResponseEntity<?> getMyEnrollments() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof UserDetails userDetails)) {
            return ResponseEntity.badRequest().body("Не удалось определить пользователя");
        }

        Student student = studentRepository.findByEmail(userDetails.getUsername()).orElse(null);
        if (student == null) {
            return ResponseEntity.badRequest().body("Студент не найден");
        }

        List<Enrollment> enrollments = enrollmentRepository.findAll()
                .stream()
                .filter(e -> e.getStudent().getId().equals(student.getId()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(enrollments);
    }
}
