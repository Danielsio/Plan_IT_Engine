package com.example.planit.mongo;

import com.example.planit.engine.AdminEngine;
import com.example.planit.engine.HolidaysEngine;
import com.example.planit.model.mongo.holiday.Holiday;
import com.example.planit.model.mongo.holiday.HolidayRepository;
import com.example.planit.model.mongo.user.User;
import com.example.planit.model.mongo.user.UserRepository;
import com.example.planit.utill.Constants;
import com.example.planit.utill.dto.DTOresponseToController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class UpdateHolidaysTest {

    @MockBean
    private HolidayRepository holidayRepo;

    @MockBean
    private HolidaysEngine holidaysEngine;

    @MockBean
    private UserRepository userRepo;

    @Autowired
    private AdminEngine adminEngine;

    private List<Holiday> holidays;

    @Value("${holidays_api_key}")
    private String holidaysApiKey;

    @BeforeEach
    public void setUp() {
        this.holidays = new ArrayList<>();
        holidays.add(new Holiday("testHoliday", "testDateOfHoliday"));
    }

    @Test
    void updateHolidays_WhenUserNotFound_ReturnsBadRequest() {
        String sub = "UserNotFoundSubID";
        when(userRepo.findUserBySubjectID(sub)).thenReturn(Optional.empty());
        // Mock the behavior of holidayRepo.findAll() to return the mocked holidays
        when(holidayRepo.findAll()).thenReturn(holidays);

        DTOresponseToController response = adminEngine.updateHolidays(sub);

        verify(userRepo, times(1)).findUserBySubjectID(sub);
        assertFalse(response.isSucceed());
        assertEquals(Constants.ERROR_USER_NOT_FOUND, response.getDetails());
        assertEquals(HttpStatus.BAD_REQUEST, response.getHttpStatus());
    }

    @Test
    void updateHolidays_WhenUserIsNotAdmin_ReturnsUnauthorized() {
        String sub = "NotAdminUserSubID";
        User user = new User();
        user.setAdmin(false); // User is not an admin
        when(userRepo.findUserBySubjectID(sub)).thenReturn(Optional.of(user));
        when(holidayRepo.findAll()).thenReturn(holidays);

        DTOresponseToController response = adminEngine.updateHolidays(sub);

        verify(userRepo, times(1)).findUserBySubjectID(sub);
        assertFalse(response.isSucceed());
        assertEquals(Constants.ERROR_UNAUTHORIZED_USER, response.getDetails());
        assertEquals(HttpStatus.UNAUTHORIZED, response.getHttpStatus());
    }

    @Test
    void updateHolidays_WhenCalendrificThrowsException_ReturnsInternalServerError() throws IOException {
        String sub = "AdminUserSubID";
        User user = new User();
        user.setAdmin(true);
        when(userRepo.findUserBySubjectID(sub)).thenReturn(Optional.of(user));

        when(holidaysEngine.getDatesOfHolidays(holidaysApiKey, Constants.ISRAEL_HOLIDAYS_CODE, 2023))
                .thenThrow((new RuntimeException("calendrific Error")));

        DTOresponseToController response = adminEngine.updateHolidays(sub);

        assertFalse(response.isSucceed());
        assertEquals(Constants.ERROR_DEFAULT, response.getDetails());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getHttpStatus());

        verify(userRepo, times(1)).findUserBySubjectID(sub);
        verify(holidayRepo, times(1)).deleteAll();
        verify(holidayRepo, times(1)).saveAll(any());
    }

    @Test
    void updateHolidays_WhenAllStepsSucceed_ReturnsOk() throws IOException {
        User user = new User();
        user.setAdmin(true);
        String sub = "AdminUserSubID";
        when(userRepo.findUserBySubjectID(sub)).thenReturn(Optional.of(user));
        when(holidaysEngine.getDatesOfHolidays(holidaysApiKey, Constants.ISRAEL_HOLIDAYS_CODE, 2023))
                .thenReturn(Collections.singleton(new Holiday("Holiday 1", "2023-01-01")));

        when(holidaysEngine.getDatesOfHolidays(holidaysApiKey, Constants.ISRAEL_HOLIDAYS_CODE, 2024))
                .thenReturn(Collections.singleton(new Holiday("Holiday 1", "2023-01-01")));

        DTOresponseToController response = adminEngine.updateHolidays(sub);

        assertTrue(response.isSucceed());
        assertEquals(Constants.NO_PROBLEM, response.getDetails());
        assertEquals(HttpStatus.OK, response.getHttpStatus());

        verify(userRepo, times(1)).findUserBySubjectID(sub);
        verify(holidayRepo, times(1)).deleteAll();
        verify(holidayRepo, times(2)).saveAll(any());
    }
}
