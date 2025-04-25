package com.example.lab2.repository;

import com.example.lab2.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {
    boolean existsByEmail(String email);
    Optional<Student> findByEmail(String email);
    List<Student> findByFirstNameIgnoreCase(String firstName);
    List<Student> findByLastNameIgnoreCase(String lastName);

    List<Student> findByGroupNameIgnoreCase(String groupName);
    List<Student> findByEmailAndGroupName(String email, String groupName);

}
