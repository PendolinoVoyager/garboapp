package com.garboapp.calendar.calendar_event;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.garboapp.calendar.calendar_event.requests.FilterSearchCalendarEventRequest;
import com.garboapp.calendar.calendar_event.requests.PatchCalendarEventRequest;
import com.garboapp.calendar.calendar_event.requests.PostCalendarEventRequest;
import com.garboapp.calendar.calendar_tag.CalendarTag;
import com.garboapp.calendar.calendar_tag.CalendarTagService;




@Service
public class CalendarEventService {

    private final CalendarEventRepository calendarEventRepository;
    private final CalendarTagService calendarTagService;

    public static final int CALENDAR_EVENT_RESULTS_PER_PAGE = 25;


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
    
    public CalendarEvent patchCalendarEvent(Integer userId, PatchCalendarEventRequest request)
        throws NoSuchElementException, AccessDeniedException
    {
        var event = calendarEventRepository.findById(request.id()).orElseThrow();
        if (event.getUserId() != userId) {
            throw new AccessDeniedException("User " + userId + " doesn't own event " + event.getId());
        }

        request.updateEventWithoutTags(event);
        if (request.tags().isPresent()) {
            List<CalendarTag> tags = calendarTagService.findOrCreateTagsByNames(request.tags().get());
            event.setTags(new ArrayList<>(tags));
        }

        return calendarEventRepository.save(event);

    }   

    public Integer deleteEvent(int userId, int eventId) throws NoSuchElementException, AccessDeniedException {
        var event = calendarEventRepository.findById(eventId).orElseThrow();
        if (event.getUserId() != userId) {
            throw new AccessDeniedException("User " + userId + " doesn't own event " + event.getId());
        }
        calendarEventRepository.delete(event);
        return eventId;

    }
    /**
     * 
     * @param userId - userId from User Service
     * @param year - Non negative year
     * @param month - Month between 1 and 12 (or Calendar.<ANYMONTH> - 1)
     * @return Empty list if not found.
     */
    public List<CalendarEvent> handleCalendarEventsByUserForYearAndMonth(Integer userId, int year, int month) {
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

        return calendarEventRepository.findDistinctByUserIdAndEventTimeBetween(userId, startDate, endDate);
    }

    @SuppressWarnings("null")
    public Page<CalendarEvent> handleFilterSearch(int userId, FilterSearchCalendarEventRequest request) {
        var pageRequest = (request.sortOrder() != null && request.sortOrder().toLowerCase().startsWith("des")) ? 
            PageRequest.of(request.page(), CALENDAR_EVENT_RESULTS_PER_PAGE, Sort.by("eventTime").descending()) :
            PageRequest.of(request.page(), CALENDAR_EVENT_RESULTS_PER_PAGE, Sort.by("eventTime").ascending());
        
        // No need to search for tags ids in this case
        if (request.tags().isEmpty()) {
            return calendarEventRepository.findDistinctByUserIdAndEventTimeBetween(
                userId, request.startDate(), request.endDate(), pageRequest);
        }

        List<Integer> tags = calendarTagService.findAllByNames(request.tags())
                    .stream()
                    .filter(Optional::isPresent)
                    .map(t -> t.get().getId())
                    .toList();

        if (request.title() != null && request.details() != null) {
            return calendarEventRepository.findDistinctByUserIdAndEventTimeBetweenAndDetailsContainingIgnoreCaseOrTitleContainingIgnoreCase
            (userId, request.startDate(), request.endDate(), request.details(), request.title(), pageRequest);
        }
        else if (request.title() != null && request.details() == null) {
            return calendarEventRepository.findDistinctByUserIdAndEventTimeBetweenAndTitleContainingIgnoreCase
            (userId, request.startDate(), request.endDate(), request.title(), pageRequest);
        }
        else if (request.details() != null) {
            return calendarEventRepository.findDistinctByUserIdAndEventTimeBetweenAndDetailsContainingIgnoreCase
            (userId, request.startDate(), request.endDate(), request.title(), pageRequest);
        }
        return calendarEventRepository.findDistinctByUserIdAndEventTimeBetweenAndTags_IdIn
         (userId, request.startDate(), request.endDate(), tags, pageRequest);
    }

}
