package com.garboapp.calendar.calendar_tag;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalendarTagRepository extends JpaRepository<CalendarTag, Integer> {
    public Optional<CalendarTag> findByName(String name);
}
