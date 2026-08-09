package com.garboapp.calendar.calendar_event;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Integer> {
    public Page<CalendarEvent> findAllByTags(Integer tag_id, Pageable pageable);
    public Page<CalendarEvent> findAllByUserIdAndTags(Integer id, Integer tag_id, Pageable pageable);
    public List<CalendarEvent> findByUserIdAndEventTimeBetween(Integer userId, Date start, Date end);

}
