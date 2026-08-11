package com.garboapp.calendar.calendar_event;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CalendarEventDTO {
    private Integer id;
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
   
   public CalendarEventDTO(CalendarEvent calendarEvent) {
        this.setId(calendarEvent.getId());
        this.setTitle(calendarEvent.getTitle());
        this.setDetails(calendarEvent.getDetails());
        this.setEventTime(calendarEvent.getEventTime());
        this.setDuration(calendarEvent.getDuration());
        this.setAllDay(calendarEvent.isAllDay());
        this.setTags(calendarEvent.getTags().stream().map(t -> t.getName()).toList());
        this.setCreatedAt(calendarEvent.getCreatedAt());
   }

}
