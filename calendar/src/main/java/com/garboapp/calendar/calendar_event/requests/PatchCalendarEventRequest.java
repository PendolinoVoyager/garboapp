package com.garboapp.calendar.calendar_event.requests;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import com.garboapp.calendar.calendar_event.CalendarEvent;

import jakarta.annotation.Nullable;



public record PatchCalendarEventRequest(
        Integer id,
        @Nullable
        Optional<Long> eventTime,
        Optional<Integer> duration,
        Optional<String> title,
        Optional<String> details,
        Optional<List<String>> tags,
        Optional<Boolean> isAllDay
) {
    public void updateEventWithoutTags(CalendarEvent event) {
        if (eventTime.isPresent()) {
            event.setEventTime(new Date(eventTime.get()));
        }
        if (duration.isPresent()) {
            event.setDuration(duration.get());
        }
        if (title.isPresent()) {
            event.setTitle(title.get());
        }
        if (details.isPresent()) {
            event.setDetails(details.get());
        }
        if (isAllDay.isPresent()) {
            event.setAllDay(isAllDay.get());
        }
    }
}