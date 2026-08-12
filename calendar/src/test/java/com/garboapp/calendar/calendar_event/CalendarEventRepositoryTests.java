package com.garboapp.calendar.calendar_event;



import static org.junit.jupiter.api.Assertions.assertThrows;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import com.garboapp.calendar.calendar_tag.CalendarTag;

import jakarta.validation.ConstraintViolationException;


@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@SuppressWarnings("null")
public class CalendarEventRepositoryTests {
    
    @Autowired
    private CalendarEventRepository calendarEventRepository; 

    @Autowired
    private TestEntityManager entityManager;
   
    private static final Integer USER_ID = 1;
    private static final Integer OTHER_USER_ID = 2;
 
    private CalendarTag workTag;
    private CalendarTag personalTag;
    private CalendarTag urgentTag;
 
    private Date jan1;
    private Date jan15;
    private Date jan31;
    private Date feb15;
 

    @BeforeEach
    void setUp() throws ParseException {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        jan1 = fmt.parse("2026-01-01");
        jan15 = fmt.parse("2026-01-15");
        jan31 = fmt.parse("2026-01-31");
        feb15 = fmt.parse("2026-02-15");
 
        workTag = persistTag("work");
        personalTag = persistTag("personal");
        urgentTag = persistTag("urgent");
    }

     private CalendarTag persistTag(String name) {
        CalendarTag tag = new CalendarTag();
        tag.setName(name);
        return entityManager.persistAndFlush(tag);
    }
 
    private CalendarEvent persistEvent(Integer userId, Date eventTime, String title, String details, CalendarTag... tags) {
        CalendarEvent event = new CalendarEvent();
        event.setUserId(userId);
        event.setEventTime(eventTime);
        event.setTitle(title);
        event.setDetails(details);
        event.setTags(Arrays.asList(tags));
        return entityManager.persistAndFlush(event);
    }
 
    private Date dateInJan(int day) {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(2026, Calendar.JANUARY, day, 0, 0, 0);
        return cal.getTime();
    }
 

    
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
        List<CalendarEvent> foundObjects = calendarEventRepository
                .findDistinctByUserIdAndEventTimeBetween(0, startDate, endDate, PageRequest.of(0, 2))
                .getContent();
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

    @Test
    void findByUserAndDateRangeAndTags_returnsOnlyMatchingEvent() {
        CalendarEvent match = persistEvent(USER_ID, jan15, "Team sync", "Discuss sprint", workTag);
        persistEvent(USER_ID, feb15, "Out of range", "wrong date", workTag);
        persistEvent(OTHER_USER_ID, jan15, "Wrong user", "belongs to other user", workTag);
        persistEvent(USER_ID, jan15, "No matching tag", "different tag", personalTag);
        Page<CalendarEvent> result = calendarEventRepository.findDistinctByUserIdAndEventTimeBetweenAndTags_IdIn(
                USER_ID, jan1, jan31, List.of(workTag.getId()),
                PageRequest.of(0, 1,  Sort.by("eventTime")));
 
        assertThat(result.getContent())
            .extracting(CalendarEvent::getId)
            .containsExactly(match.getId());

    }

    @Test
    void findByUserAndDateRangeAndNoTags_returnsAll() {
        CalendarEvent matchNoTagsAtAll = persistEvent(USER_ID, jan15, "Team sync", "Untagged");
        CalendarEvent matchAnyTagWorks = persistEvent(USER_ID, jan15, "Work stuff", "Tagged", workTag);
        persistEvent(OTHER_USER_ID, jan15, "Wrong user", "belongs to other user", workTag);
        Page<CalendarEvent> result = calendarEventRepository.findDistinctByUserIdAndEventTimeBetween(
                USER_ID, jan1, jan31,
                PageRequest.of(0, 2,  Sort.by("eventTime")));
 
        assertThat(result.getContent())
            .extracting(CalendarEvent::getId)
            .containsExactlyInAnyOrder(matchNoTagsAtAll.getId(), matchAnyTagWorks.getId());
    }
 
    @Test
    void findByUserAndDateRangeAndTags_matchesEventWithAnyOfMultipleTags() {
        CalendarEvent taggedWork = persistEvent(USER_ID, jan15, "Work item", "details", workTag);
        CalendarEvent taggedUrgent = persistEvent(USER_ID, dateInJan(16), "Urgent item", "details", urgentTag);
        persistEvent(USER_ID, jan15, "Untagged relevant", "details", personalTag);
 
        Page<CalendarEvent> result = calendarEventRepository.findDistinctByUserIdAndEventTimeBetweenAndTags_IdIn(
                USER_ID, jan1, jan31, List.of(workTag.getId(), urgentTag.getId()),
                PageRequest.of(0, 10,  Sort.by("eventTime")));
 
        assertThat(result.getContent())
                .extracting(CalendarEvent::getId)
                .containsExactlyInAnyOrder(taggedWork.getId(), taggedUrgent.getId());
    }
 
    @Test
    void findByUserAndDateRangeAndTags_returnsEmptyPageWhenNoMatches() {
        persistEvent(USER_ID, jan15, "Some event", "details", personalTag);
 
        Page<CalendarEvent> result = calendarEventRepository.findDistinctByUserIdAndEventTimeBetweenAndTags_IdIn(
                USER_ID, jan1, jan31, List.of(workTag.getId()),
                PageRequest.of(0, 10,  Sort.by("eventTime")));
 
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }
 
    @Test
    void findByUserAndDateRangeAndTags_respectsPagination() {
        for (int day = 1; day <= 5; day++) {
            persistEvent(USER_ID, dateInJan(day), "Event " + day, "details", workTag);
        }
 
        PageRequest firstPage = PageRequest.of(0, 2, Sort.by("eventTime"));
        Page<CalendarEvent> page1 = calendarEventRepository.findDistinctByUserIdAndEventTimeBetweenAndTags_IdIn(
                USER_ID, jan1, jan31, List.of(workTag.getId()), firstPage);
 
        assertThat(page1.getContent()).hasSize(2);
        assertThat(page1.getTotalElements()).isEqualTo(5);
        assertThat(page1.getTotalPages()).isEqualTo(3);
    }
 
    // ---------------------------------------------------------------
    // findDistinctByUserIdAndEventTimeBetweenAndTitleContainingIgnoreCaseAndTags_IdIn
    // ---------------------------------------------------------------
 
    @Test
    void findByTitleContaining_matchesCaseInsensitiveSubstring() {
        CalendarEvent match = persistEvent(USER_ID, jan15, "Quarterly Review Meeting", "details", workTag);
        persistEvent(USER_ID, jan15, "Lunch break", "details", workTag);
 
        Page<CalendarEvent> result = calendarEventRepository
                .findDistinctByUserIdAndEventTimeBetweenAndTitleContainingIgnoreCase(
                        USER_ID, jan1, jan31, "review",
                        PageRequest.of(0, 10,  Sort.by("eventTime")));
 
        assertThat(result.getContent())
                .extracting(CalendarEvent::getId)
                .containsExactly(match.getId());
    }
 
    @Test
    void findByTitleContaining_excludesWhenTitleDoesNotMatch() {
        persistEvent(USER_ID, jan15, "Standup", "details", workTag);
 
        Page<CalendarEvent> result = calendarEventRepository
                .findDistinctByUserIdAndEventTimeBetweenAndTitleContainingIgnoreCase(
                        USER_ID, jan1, jan31, "retro",
                        PageRequest.of(0, 10,  Sort.by("eventTime")));
 
        assertThat(result.getContent()).isEmpty();
    }
 
   
 
    @Test
    void findByTitleContaining_excludesWhenOutsideDateRange() {
        persistEvent(USER_ID, feb15, "Quarterly Review", "details", workTag);
 
        Page<CalendarEvent> result = calendarEventRepository
                .findDistinctByUserIdAndEventTimeBetweenAndTitleContainingIgnoreCase(
                        USER_ID, jan1, jan31, "review",
                        PageRequest.of(0, 10,  Sort.by("eventTime")));
 
        assertThat(result.getContent()).isEmpty();
    }
 
    // ---------------------------------------------------------------
    // findDistinctByUserIdAndEventTimeBetweenAndDetailsContainingIgnoreCaseAndTags_IdIn
    // ---------------------------------------------------------------
 
    @Test
    void findByDetailsContaining_matchesCaseInsensitiveSubstring() {
        CalendarEvent match = persistEvent(USER_ID, jan15, "Meeting", "Discuss BUDGET planning for Q1", workTag);
        persistEvent(USER_ID, jan15, "Meeting 2", "Unrelated notes", workTag);
 
        Page<CalendarEvent> result = calendarEventRepository
                .findDistinctByUserIdAndEventTimeBetweenAndDetailsContainingIgnoreCase(
                        USER_ID, jan1, jan31, "budget",
                        PageRequest.of(0, 10,  Sort.by("eventTime")));
 
        assertThat(result.getContent())
                .extracting(CalendarEvent::getId)
                .containsExactly(match.getId());

    }
 
    @Test
    void findByDetailsContaining_excludesEventsOutsideDateRange() {
        persistEvent(USER_ID, feb15, "Meeting", "Discuss budget planning", workTag);
 
        Page<CalendarEvent> result = calendarEventRepository
                .findDistinctByUserIdAndEventTimeBetweenAndDetailsContainingIgnoreCase(
                        USER_ID, jan1, jan31, "budget",
                        PageRequest.of(0, 10,  Sort.by("eventTime")));
 
        assertThat(result.getContent()).isEmpty();
    }
 
    @Test
    void findByDetailsContaining_excludesOtherUsersEvents() {
        persistEvent(OTHER_USER_ID, jan15, "Meeting", "Discuss budget planning", workTag);
 
        Page<CalendarEvent> result = calendarEventRepository
                .findDistinctByUserIdAndEventTimeBetweenAndDetailsContainingIgnoreCase(
                        USER_ID, jan1, jan31, "budget",
                        PageRequest.of(0, 10,  Sort.by("eventTime")));
 
        assertThat(result.getContent()).isEmpty();
    }

}
