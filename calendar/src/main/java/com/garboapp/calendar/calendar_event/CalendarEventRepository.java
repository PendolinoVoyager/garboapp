package com.garboapp.calendar.calendar_event;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Integer> {
    public Optional<CalendarEvent> findById(Integer id);
    
    /**
     * Repository method for all events, not pageable, for short durations without pages. 
     * For pageable searching method see findDistinctByUserIdAndEventTimeBetween*.
     * @param userId
     * @param start
     * @param end
     * @param pageable
     * @return
     */
    public List<CalendarEvent> findDistinctByUserIdAndEventTimeBetween(
        Integer userId, Date start, Date end);

    //no tags
    public Page<CalendarEvent> findDistinctByUserIdAndEventTimeBetween(
        Integer userId, Date start, Date end,Pageable pageable);
    //tags
    public Page<CalendarEvent> findDistinctByUserIdAndEventTimeBetweenAndTags_IdIn(
        Integer userId, Date start, Date end, List<Integer> tags, Pageable pageable);

    public Page<CalendarEvent> findDistinctByUserIdAndEventTimeBetweenAndTitleContainingIgnoreCase(
        Integer userId, Date start, Date end, String title, Pageable pageable);
    public Page<CalendarEvent> findDistinctByUserIdAndEventTimeBetweenAndDetailsContainingIgnoreCase(
        Integer userId, Date start, Date end, String details, Pageable pageable);
    public Page<CalendarEvent> findDistinctByUserIdAndEventTimeBetweenAndDetailsContainingIgnoreCaseOrTitleContainingIgnoreCase(
        Integer userId, Date start, Date end, String details, String title, Pageable pageable);
}
