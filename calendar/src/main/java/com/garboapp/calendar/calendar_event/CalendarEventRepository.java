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
    public Page<CalendarEvent> findAllByTags(Integer tag_id, Pageable pageable);
    public Page<CalendarEvent> findAllByUserIdAndTags(Integer id, Integer tag_id, Pageable pageable);
    public List<CalendarEvent> findAllByUserIdAndEventTimeBetween(Integer userId, Date start, Date end);

}
