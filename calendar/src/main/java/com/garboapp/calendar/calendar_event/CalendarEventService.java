package com.garboapp.calendar.calendar_event;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.garboapp.calendar.calendar_event.request_response.PostCalendarEventRequest;
import com.garboapp.calendar.calendar_tag.CalendarTag;
import com.garboapp.calendar.calendar_tag.CalendarTagService;


@Service
public class CalendarEventService {

    private final CalendarEventRepository calendarEventRepository;
    private final CalendarTagService calendarTagService;

    private final static Logger logger = Logger.getLogger(CalendarTag.class.getName());

    CalendarEventService(
        CalendarEventRepository calendarEventRepository,
        CalendarTagService calendarTagService
     ) {
        this.calendarEventRepository = calendarEventRepository;
        this.calendarTagService = calendarTagService;
    }

      public CalendarEvent saveCalendarEvent(
            Integer userId,
            PostCalendarEventRequest request) {
        // Create tags if they don't exists first.
        // They can sit there even if the request turns out bad
        var tags = calendarTagService.findOrCreateTagsByNames(request.tags() == null ? List.of() : request.tags());
        var newEvent = CalendarEvent.builder()
                        .userId(userId)
                        .title(request.title())
                        .details(request.details())
                        .eventTime(new Date(request.eventTime()))
                        .duration(request.duration())
                        .isAllDay(request.isAllDay())
                        .tags(tags)
                        .build();
        return calendarEventRepository.saveAndFlush(newEvent);
    }
    /**
     * 
     * @param userId - userId from User Service
     * @param year - Non negative year
     * @param month - Month between 1 and 12 (or Calendar.<ANYMONTH> - 1)
     * @return Empty list if not found.
     */
    public List<CalendarEvent> getCalendarEventsByUserForYearAndMonth(Integer userId, int year, int month) {
        if (month <= 0 || month > 12 || year < 0) {
            logger.warning("Provided wrong year on month in calendarEvent query : " + month + "." + year);
            return List.of();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, month - 1, 1); // Calendar months are 0-indexed
        Date startDate = calendar.getTime();

        calendar.add(Calendar.MONTH, 1);
        Date endDate = calendar.getTime();

        return calendarEventRepository.findByUserIdAndEventTimeBetween(userId, startDate, endDate);
    }
}
