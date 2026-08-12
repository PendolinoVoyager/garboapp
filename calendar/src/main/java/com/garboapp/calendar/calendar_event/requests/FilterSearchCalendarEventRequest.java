package com.garboapp.calendar.calendar_event.requests;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.Valid;

import jakarta.annotation.Nullable;

@Valid
public record FilterSearchCalendarEventRequest (
    Integer page,
    @DateTimeFormat(pattern = "dd.MM.yyyy")
    @Nullable Date startDate,
    @DateTimeFormat(pattern = "dd.MM.yyyy")
    @Nullable Date endDate,
    @Nullable List<String> tags,
    @Nullable String title,
    @Nullable String details,
    @Nullable String sortOrder
) {
    public FilterSearchCalendarEventRequest {
        if (page == null) page = 0;
        if (startDate == null) startDate = new Date(0);
        // PSQL max is 294276 AD with microsecond resolution, let's stick with that
        if (endDate == null) {
            var calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, 294275);
            endDate = calendar.getTime();
        }
        if (title != null && title.isEmpty()) title = null;
        if (details != null && details.isEmpty()) details = null;
        if (tags == null) tags = List.of();
    }
}