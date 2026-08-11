package com.garboapp.calendar.calendar_event;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.garboapp.calendar.calendar_event.requests.PostCalendarEventRequest;
import com.garboapp.calendar.config.SecurityConfig;

import tools.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * All tags stay empty, not testing that here and it's a bit finnicky.
 * CalendarControllerTests
 */
@Import(SecurityConfig.class) // Important as Spring Security will slap authorized on all endpoints
@WebMvcTest(CalendarEventController.class)
public class CalendarControllerTests {
    @Mock
    private final CalendarEventController handler = new CalendarEventController();

    @Autowired
    private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @MockitoBean
    private CalendarEventService calendarEventService;

   
    @Test
    public void testCreateEvent() throws Exception {
        var currentDate = new Date();
        var request = new PostCalendarEventRequest(
            currentDate.getTime() + 1000 * 60 * 60,
            60,
            "My Title",
            "Im doing something in a day.",
            null,
            false
        );

        var savedEvent = CalendarEvent.builder()
        .eventTime(new Date(currentDate.getTime() + 1000 * 60 * 60))
        .duration(request.duration())
        .title(request.title())
        .details(request.details())
        .tags(List.of()) 
        .build();

        when(calendarEventService.saveCalendarEvent(any(Integer.class), any(PostCalendarEventRequest.class)))
                .thenReturn(savedEvent);


        mockMvc.perform(post("/api/v1/public/calendarEvents")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.title").value("My Title"))
        .andExpect(jsonPath("$.details").value("Im doing something in a day."))
        .andExpect(jsonPath("$.duration").value(60));
        
    }

    @Test
    public void testCreateEventFailNoEventTime() throws Exception {
        var request = new PostCalendarEventRequest(
            null,
            60,
            "My Title",
            "Im doing something in a day.",
            null,
            false
        );

        var savedEvent = CalendarEvent.builder()
        .eventTime(new Date())
        .duration(request.duration())
        .title(request.title())
        .details(request.details())
        .tags(List.of()) 
        .build();
         when(calendarEventService.saveCalendarEvent(any(Integer.class), any(PostCalendarEventRequest.class)))
                .thenReturn(savedEvent);
        
        mockMvc.perform(post("/api/v1/public/calendarEvents")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().is(400));
        
    }
}
