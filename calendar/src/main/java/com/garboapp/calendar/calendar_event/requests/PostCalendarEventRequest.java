package com.garboapp.calendar.calendar_event.requests;

import java.util.List;

import org.jspecify.annotations.Nullable;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Valid
public record PostCalendarEventRequest(
    @jakarta.validation.constraints.Min(0)
    @NotNull 
    Long eventTime,
    int duration,
    String title,
    String details,
    @Nullable
    List<String> tags,
    boolean isAllDay
) {
    public PostCalendarEventRequest {
        if (tags == null) tags = List.of();
    }
}