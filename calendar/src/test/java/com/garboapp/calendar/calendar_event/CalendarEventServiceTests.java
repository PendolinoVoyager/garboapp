package com.garboapp.calendar.calendar_event;




import java.util.Calendar;
import java.util.List;

import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;



@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class CalendarEventServiceTests {
    
    @Autowired
    private CalendarEventRepository calendarEventRepository;

    @Autowired 
    private CalendarEventService calendarEventService;
 
    
    @Test
    public void findsAllEventsInYearAndMonthByUser() {
        var calendar = Calendar.getInstance();
        calendar.set(2022, Calendar.AUGUST, 1);
        CalendarEvent ceAugust2022_1 = CalendarEvent.builder().userId(0).eventTime(calendar.getTime()).build();
        calendar.set(2022, Calendar.AUGUST, 23);
        CalendarEvent ceAugust2022_2 = CalendarEvent.builder().userId(0).eventTime(calendar.getTime()).build();
        calendar.set(1995, Calendar.AUGUST, 1);
        CalendarEvent ceNotAugust2022 = CalendarEvent.builder().userId(0).eventTime(calendar.getTime()).build();
        

        //act
        var savedObjectsInAugust2022 = calendarEventRepository.saveAll(List.of(ceAugust2022_1, ceAugust2022_2));
        calendarEventRepository.save(ceNotAugust2022);
        calendarEventRepository.flush();

        List<CalendarEvent> foundEvents = calendarEventService.getCalendarEventsByUserForYearAndMonth(0, 2022, 8);

        //assert
        assert(foundEvents.containsAll(savedObjectsInAugust2022));
    }


}
