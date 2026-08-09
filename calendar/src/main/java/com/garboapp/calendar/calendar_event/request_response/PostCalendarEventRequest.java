package com.garboapp.calendar.calendar_event.request_response;

import java.util.List;

import javax.validation.constraints.NotNull;



public record PostCalendarEventRequest(
        @NotNull long eventTime,
        int duration,
        String title,
        String details,
        List<String> tags,
        boolean isAllDay
) {
    public PostCalendarEventRequest {
        if (details == null) details = "";
        if (tags == null) tags = List.of();
    }
}