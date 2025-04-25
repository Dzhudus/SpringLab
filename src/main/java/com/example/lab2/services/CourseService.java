package com.example.lab2.services;

import com.example.lab2.entities.Course;
import com.example.lab2.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;

    public Page<Course> getCourses(Pageable pageable) {
        return courseRepository.findAll(pageable);
    }
}