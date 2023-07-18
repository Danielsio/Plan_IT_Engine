package com.example.planit.controller;

import com.example.planit.engine.AdminEngine;
import com.example.planit.model.mongo.course.Course;
import com.example.planit.utill.dto.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/admin")
public class AdminController {

    public static Logger logger = LogManager.getLogger(AdminController.class);
    @Autowired
    private AdminEngine adminEngine;

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping(value = "/courses")
    public ResponseEntity<DTOcoursesResponseToClient> getAllCourses(@RequestParam String sub) {
        DTOcoursesResponseToController dtOcoursesResponseToController = adminEngine.getAllCoursesFromDB(sub);

        return ResponseEntity.status(dtOcoursesResponseToController.getHttpStatus())
                .body(new DTOcoursesResponseToClient(dtOcoursesResponseToController.isSucceed(),
                        dtOcoursesResponseToController.getDetails(), dtOcoursesResponseToController.getCourses()));
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping(value = "/courses/{courseId}")
    public ResponseEntity<DTOcoursesResponseToClient> getCourse(@PathVariable String courseId, @RequestParam String sub) {
        DTOcoursesResponseToController dtOcoursesResponseToController = adminEngine.getCourseFromDB(courseId, sub);

        return ResponseEntity.status(dtOcoursesResponseToController.getHttpStatus())
                .body(new DTOcoursesResponseToClient(dtOcoursesResponseToController.isSucceed(),
                        dtOcoursesResponseToController.getDetails(), dtOcoursesResponseToController.getCourses()));
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping(value = "/courses")
    public ResponseEntity<DTOstatus> addCourse(@RequestBody Course course, @RequestParam String sub) {
        DTOcoursesResponseToController dtOcoursesResponseToController = adminEngine.addCourseToDB(course, sub);

        return ResponseEntity.status(dtOcoursesResponseToController.getHttpStatus())
                .body(new DTOstatus(dtOcoursesResponseToController.isSucceed(),
                        dtOcoursesResponseToController.getDetails()));
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PutMapping(value = "/courses")
    public ResponseEntity<DTOstatus> updateCourse(@RequestBody Course course, @RequestParam String sub) {
        DTOcoursesResponseToController dtOcoursesResponseToController = adminEngine.updateCourseInDB(course, sub);

        return ResponseEntity.status(dtOcoursesResponseToController.getHttpStatus())
                .body(new DTOstatus(dtOcoursesResponseToController.isSucceed(),
                        dtOcoursesResponseToController.getDetails()));
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping(value = "/users")
    public ResponseEntity<DTOstatus> getAllUsers(@RequestParam String sub) {
        DTOusersResponseToController dtOusersResponseToController = adminEngine.getAllUsersFromDB(sub);

        return ResponseEntity.status(dtOusersResponseToController.getHttpStatus())
                .body(new DTOusersResponseToClient(dtOusersResponseToController.isSucceed(),
                        dtOusersResponseToController.getDetails(), dtOusersResponseToController.getUsers()));
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PutMapping(value = "/make-user-admin")
    public ResponseEntity<DTOstatus> makeUserAdmin(@RequestParam String sub, @RequestParam String userSubId) {
        DTOusersResponseToController dtOusersResponseToController = adminEngine.makeUserAdminInDB(sub, userSubId);

        return ResponseEntity.status(dtOusersResponseToController.getHttpStatus())
                .body(new DTOstatus(dtOusersResponseToController.isSucceed(),
                        dtOusersResponseToController.getDetails()));
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PutMapping(value = "/update-holidays")
    public ResponseEntity<DTOstatus> updateHolidays(@RequestParam String sub) {

        DTOresponseToController dtoResponseToController = adminEngine.updateHolidays(sub);

        return ResponseEntity.status(dtoResponseToController.getHttpStatus())
                .body(new DTOstatus(dtoResponseToController.isSucceed(), dtoResponseToController.getDetails()));
    }

}

