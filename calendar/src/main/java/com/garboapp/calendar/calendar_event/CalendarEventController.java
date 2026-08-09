package com.garboapp.calendar.calendar_event;

import java.util.logging.Logger;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.garboapp.calendar.auth.UserPrincipal;
import com.garboapp.calendar.calendar_event.request_response.CalendarEventResponse;
import com.garboapp.calendar.calendar_event.request_response.PostCalendarEventRequest;
import com.garboapp.calendar.utils.NotOkResponse;
import com.garboapp.calendar.utils.NotOkResponseReasonCode;


@RestController
@RequestMapping("/api/v1/public/calendarEvents")
public class CalendarEventController {
    @Autowired
    private CalendarEventService calendarEventService;

    private final static Logger logger = Logger.getLogger(CalendarEventController.class.getName());

    @PostMapping
    public ResponseEntity<Object> createCalendarEvent(
        @AuthenticationPrincipal UserPrincipal authPrincipal,
        @RequestBody @Valid PostCalendarEventRequest request) {
        try {
            var createdEvent = calendarEventService.saveCalendarEvent(authPrincipal.userId(), request);
            logger.info("Created event: " + createdEvent.getId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(
                        CalendarEventResponse.fromCalendarEventEntity(createdEvent)
                    );
        }
        catch (Exception e) {
            logger.warning("Failed to create event: " + e);
            return ResponseEntity.badRequest().body(
            new NotOkResponse("Cannot create event: " + e.getMessage(),
             NotOkResponseReasonCode.UNKNOWN_ERROR));
        }
    }

    @GetMapping
    public ResponseEntity<Object> getCalendarEventsForUserByYearAndDate(
        @AuthenticationPrincipal UserPrincipal authPrincipal,
        @RequestParam int year,
        @RequestParam int month

    ) {
        var events = calendarEventService.getCalendarEventsByUserForYearAndMonth(authPrincipal.userId(), year, month);
        return ResponseEntity.ok(events);
    }

}
