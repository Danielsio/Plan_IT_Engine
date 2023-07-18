package com.example.planit.utill.dto;

import com.example.planit.model.mongo.user.StudyPlan;
import org.springframework.http.HttpStatus;

public class DTOstudyPlanResponseToController extends DTOresponseToController {
    private final StudyPlan studyPlan;

    public DTOstudyPlanResponseToController(boolean isSucceed, String details, HttpStatus httpStatus, StudyPlan studyPlan) {
        super(isSucceed, details, httpStatus);
        this.studyPlan = studyPlan;
    }

    public DTOstudyPlanResponseToController(boolean isSucceed, String details, HttpStatus httpStatus) {
        super(isSucceed, details,httpStatus);
        this.studyPlan = null;
    }

    public StudyPlan getStudyPlan() {
        return studyPlan;
    }
}
