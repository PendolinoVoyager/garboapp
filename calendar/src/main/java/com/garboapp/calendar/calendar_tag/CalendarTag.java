package com.garboapp.calendar.calendar_tag;


import java.util.ArrayList;
import java.util.List;


import com.garboapp.calendar.calendar_event.CalendarEvent;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="calendarTags")
@Getter
@Setter
@AllArgsConstructor    
@NoArgsConstructor
@Builder
public class CalendarTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, length = 32, unique = true)
    private String name;

    @ManyToMany(mappedBy = "tags", cascade = CascadeType.MERGE)
    private final List<CalendarEvent> calendarEntries = new ArrayList<>();

}
