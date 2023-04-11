package com.example.planit.controller;

import com.example.planit.engine.CalendarEngine;
import com.example.planit.engine.HolidaysEngine;
import com.example.planit.model.mongo.course.CoursesRepository;
import com.example.planit.model.mongo.user.UserRepository;
import com.example.planit.utill.Constants;
import com.example.planit.utill.dto.*;
import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Set;

import static com.example.planit.utill.Constants.ISRAEL_HOLIDAYS_CODE;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class CalendarController {

    @Autowired
    private Environment env;

    @Autowired
    private CoursesRepository courseRepo;

    @Autowired
    private UserRepository userRepo;

    public static Logger calendarLogger = LogManager.getLogger(Constants.CALENDAR_LOGGER_NAME);

    private Set<String> holidaysDatesCurrentYear;
    private Set<String> holidaysDatesNextYear;

    private CalendarEngine calendarEngine;

    @PostConstruct
    private void init() {

        // get CLIENT_ID & CLIENT_SECRET values from environment
        String CLIENT_ID = env.getProperty("spring.security.oauth2.client.registration.google.client-id");
        String CLIENT_SECRET = env.getProperty("spring.security.oauth2.client.registration.google.client-secret");

        // initialize CalendarEngine
        this.calendarEngine = new CalendarEngine(CLIENT_ID, CLIENT_SECRET, userRepo, courseRepo, holidaysDatesCurrentYear, holidaysDatesNextYear);

        // extract the holidays dates as iso format and return it in a set of string(iso format) (for current year and the next year).
        holidaysDatesCurrentYear = HolidaysEngine.getDatesOfHolidays(env.getProperty("holidays_api_key"), ISRAEL_HOLIDAYS_CODE, ZonedDateTime.now().getYear());
        holidaysDatesNextYear = HolidaysEngine.getDatesOfHolidays(env.getProperty("holidays_api_key"), ISRAEL_HOLIDAYS_CODE, ZonedDateTime.now().getYear() + 1);
    }

    /**
     * Scan the user's Calendar to get list of events and check to see if user has fullDayEvents existed.
     *
     * @param sub user's sub value to search the User on DB & get preferences.
     * @return ResponseEntity<List < Event>> we return list of events in a case of full day events found, otherwise we generate the calendar.
     */
    @PostMapping(value = "/scan")
    public ResponseEntity<DTOscanResponseToClient> scanUserEvents(@RequestParam String sub, @RequestParam String start, @RequestParam String end) {

        long s = System.currentTimeMillis();
        calendarLogger.info("User " + sub + " has requested scan");

        DTOscanResponseToController scanResponseToController = calendarEngine.scanUserEvents(sub, start, end);

        long t = System.currentTimeMillis();
        long res = t - s;
        calendarLogger.info("scan time is " + res + " ms");

        return ResponseEntity.status(scanResponseToController.getHttpStatus())
                .body(new DTOscanResponseToClient(scanResponseToController.isSucceed(),
                        scanResponseToController.getDetails(),
                        scanResponseToController.getFullDayEvents(), scanResponseToController.getStudyPlan()));
    }

    /**
     * this endpoint re-scan the user Calendar events, deals with the full days events that has been found and generates the plan it calendar.
     *
     * @param sub       user's sub value to search the User on DB & get preferences
     * @param decisions array of boolean values representing
     * @return ResponseEntity<String> this method not suppose to fail unless it's been called externally
     */
    @PostMapping(value = "/generate", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DTOstatus> generateStudyEvents(@RequestParam String sub, @RequestParam String start, @RequestParam String end, @RequestBody Map<Long, Boolean> decisions) {

        DTOgenerateResponseToController generateResponseToController = calendarEngine.generateStudyEvents(sub, start, end, decisions);
        return ResponseEntity.status(generateResponseToController.getHttpStatus())
                .body(new DTOgenerateResponseToClient(generateResponseToController.isSucceed(),
                        generateResponseToController.getDetails(), generateResponseToController.getStudyPlan()));
    }
}
