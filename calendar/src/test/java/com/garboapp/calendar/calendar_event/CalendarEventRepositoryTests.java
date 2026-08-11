package com.garboapp.calendar.calendar_event;



import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import jakarta.validation.ConstraintViolationException;



@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class CalendarEventRepositoryTests {
    
    @Autowired
    private CalendarEventRepository calendarEventRepository; 
    
    @Test
    public void findByUserIdAndEventTimeBetweenTest() {

        var calendar = Calendar.getInstance();
        calendar.set(2022, Calendar.AUGUST, 3);
        var startDate = calendar.getTime();
        CalendarEvent ceAugust2022_1 = CalendarEvent.builder().userId(0).eventTime(calendar.getTime()).build();
        calendar.set(2022, Calendar.AUGUST, 23);
        var endDate = calendar.getTime();
        // Checking also inclusive for day

        CalendarEvent ceAugust2022_2 = CalendarEvent.builder().userId(0).eventTime(calendar.getTime()).build();
        calendar.set(1995, Calendar.AUGUST, 1);
        CalendarEvent ceNotAugust2022 = CalendarEvent.builder().userId(0).eventTime(calendar.getTime()).build();
        

        //act
        List<CalendarEvent> savedObjectsInBetweenDates = calendarEventRepository.saveAll(List.of(ceAugust2022_1, ceAugust2022_2));
        calendarEventRepository.save(ceNotAugust2022);
        calendarEventRepository.flush();
        List<CalendarEvent> foundObjects = calendarEventRepository.findAllByUserIdAndEventTimeBetween(0, startDate, endDate);
        
        //assert
        assert(foundObjects.containsAll(savedObjectsInBetweenDates));
    }

    @Test
    public void testCalendarEventMustHaveUserId() {
        CalendarEvent ce = CalendarEvent.builder().isAllDay(true).eventTime(new Date()).build();

        assertThrows(ConstraintViolationException.class, () -> {
            calendarEventRepository.save(ce);
            calendarEventRepository.flush();
        });
    }

    @Test
    public void testCalendarEventEventTimeNotNull() {
        CalendarEvent ce = CalendarEvent.builder().isAllDay(true).build();

        assertThrows(ConstraintViolationException.class, () -> {
            calendarEventRepository.save(ce);
            calendarEventRepository.flush();
        });
    }


}
