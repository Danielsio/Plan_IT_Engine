package com.example.planit.utill.dto;

import org.springframework.http.HttpStatus;

public class DTOresponseToController extends DTOstatus {

    private final HttpStatus httpStatus;

    public DTOresponseToController(boolean isSucceed, String details, HttpStatus httpStatus) {
        super(isSucceed, details);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
