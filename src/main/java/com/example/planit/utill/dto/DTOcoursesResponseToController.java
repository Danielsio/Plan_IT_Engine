package com.example.planit.utill.dto;

import com.example.planit.model.mongo.course.Course;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

public class DTOcoursesResponseToController extends DTOresponseToController {
    private final List<Course> courses;

    public DTOcoursesResponseToController(boolean isSucceed, String details, HttpStatus httpStatus, List<Course> courses) {
        super(isSucceed, details, httpStatus);
        this.courses = courses;
    }

    public DTOcoursesResponseToController(boolean isSucceed, String details, HttpStatus httpStatus) {
        super(isSucceed, details, httpStatus);
        this.courses = new ArrayList<>();
    }

    public List<Course> getCourses() {
        return courses;
    }
}
