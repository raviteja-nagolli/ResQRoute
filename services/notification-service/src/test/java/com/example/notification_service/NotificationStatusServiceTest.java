package com.example.notification_service;

import com.example.notification_service.service.NotificationStatusService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

public class NotificationStatusServiceTest {
    @Test
    void pendingCanTransitionToSent(){
        NotificationStatusService notificationStatusService=new NotificationStatusService();
        boolean result=notificationStatusService.isValidTransition("PENDING","SENT");
        assertTrue(result);
    }

    @Test
    void pendingCannotTransitionDirectlyToRead(){
        NotificationStatusService notificationStatusService=new NotificationStatusService();
        boolean result=notificationStatusService.isValidTransition("PENDING","READ");
        assertFalse(result);
    }

    @ParameterizedTest
    @CsvSource({
            "PENDING, SENT, true",
            "PENDING, FAILED, true",
            "SENT, READ, true",
            "FAILED , PENDING, true"
    })
    void shouldValidateAllowedTransitions(String currentStatus, String newStatus, boolean expected){
        NotificationStatusService service=new NotificationStatusService();
        boolean actual=service.isValidTransition(currentStatus,newStatus);
        assertEquals(expected,actual);
    }

    @ParameterizedTest
    @CsvSource({
            "PENDING, PENDING",
            "PENDING, READ",
            "SENT, FAILED",
            "SENT, PENDING",
            "FAILED, SENT",
            "READ, READ",
            "READ, PENDING"
    })
    void shouldRejectInvalidTransition(String currentStatus, String newStatus){
        NotificationStatusService service=new NotificationStatusService();
        boolean actual=service.isValidTransition(currentStatus,newStatus);
        assertFalse(actual);
    }

    @Test
    void unknownCurrentStatusShouldBeRejected(){
        NotificationStatusService service=new NotificationStatusService();
        boolean actual=service.isValidTransition("UNKNOWN","SENT");
        assertFalse(actual);
    }
}
