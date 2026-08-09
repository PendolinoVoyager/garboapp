package com.garboapp.calendar.calendar_event.request_response;

import java.util.Date;
import java.util.List;

import com.garboapp.calendar.calendar_event.CalendarEvent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CalendarEventResponse {
    
    /**
     * Duration in minutes.
    */
   private String title;
   private String details;
   private Date eventTime;
   private int duration;
   private boolean isAllDay;
   private List<String> tags;
   private Date createdAt;

   public static CalendarEventResponse fromCalendarEventEntity(CalendarEvent calendarEvent) {
        return  CalendarEventResponse.builder()
        .title(calendarEvent.getTitle())
        .details(calendarEvent.getDetails())
        .eventTime(calendarEvent.getEventTime())
        .duration(calendarEvent.getDuration())
        .isAllDay(calendarEvent.isAllDay())
        .tags(calendarEvent.getTags().stream().map(t -> t.getName()).toList())
        .createdAt(calendarEvent.getCreatedAt())
        .build();
        
   }
}
