package com.garboapp.calendar.calendar_event;



import java.util.NoSuchElementException;
import java.util.logging.Logger;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.garboapp.calendar.auth.UserPrincipal;
import com.garboapp.calendar.calendar_event.requests.PatchCalendarEventRequest;
import com.garboapp.calendar.calendar_event.requests.PostCalendarEventRequest;
import com.garboapp.calendar.utils.NotOkResponse;
import com.garboapp.calendar.utils.NotOkResponseReasonCode;

import jakarta.servlet.ServletRequest;


@RestController
@RequestMapping("/api/v1/public/calendarEvents")
public class CalendarEventController {
    @Autowired
    private CalendarEventService calendarEventService;

    private final static Logger logger = Logger.getLogger(CalendarEventController.class.getName());

    @PostMapping
    public ResponseEntity<Object> createCalendarEvent(
        @AuthenticationPrincipal UserPrincipal authPrincipal,
        @Validated @RequestBody PostCalendarEventRequest request) {
        var createdEvent = calendarEventService.saveCalendarEvent(authPrincipal.userId(), request);
        logger.info("Created event: " + createdEvent.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                    new CalendarEventDTO(createdEvent)
        );
        
    }

    @GetMapping
    public ResponseEntity<Object> getCalendarEventsForUserByYearAndDate(
        @AuthenticationPrincipal UserPrincipal authPrincipal,
        @RequestParam int year,
        @RequestParam int month

    ) {
        var events = calendarEventService.getCalendarEventsByUserForYearAndMonth(authPrincipal.userId(), year, month);
        return ResponseEntity.ok(events.stream().map(e -> new CalendarEventDTO(e)).toList());
    }

    @PatchMapping
    public ResponseEntity<Object> patchCalendarEvent(
        @AuthenticationPrincipal UserPrincipal authPrincipal,
        @Validated @RequestBody PatchCalendarEventRequest request
    )
    {
        var updatedEvent = calendarEventService.patchCalendarEvent(authPrincipal.userId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(
                new CalendarEventDTO(updatedEvent)
            );
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<NotOkResponse> handleNoSuchElementException(NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(NotOkResponse.builder()
                .reasonCode(NotOkResponseReasonCode.NOT_SPECIFIED)
                .message("Event with specified id does not exist").build());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<NotOkResponse> handleAccessDeniedException(ServletRequest request, AccessDeniedException e) {
        logger.warning(request.getRemoteAddr() + " attempted access to not-owned event.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(NotOkResponse.builder()
                .reasonCode(NotOkResponseReasonCode.DOES_NOT_OWN_RESOURCE)
                .message("User does not own this event.").build());
    }

}
