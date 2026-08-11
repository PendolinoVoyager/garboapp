package com.garboapp.calendar.calendar_event;

import java.util.Date;
import java.util.List;


import com.garboapp.calendar.calendar_tag.CalendarTag;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name="calendarEvents", indexes = @Index(columnList = "userId"))
@Getter
@Setter
@AllArgsConstructor    
@NoArgsConstructor
@Builder
public class CalendarEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Decoupled with user database, no relation unfortunately
    @Column(name = "userId", nullable = false)
    @NotNull(message = "Event must have the owner's userId")
    private Integer userId;

    @Column(name = "title", nullable = true, length=50)
    private String title;

    @Column(name = "details", nullable = true, length=10000)
    private String details;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(
        name = "calendarEventTags",
        joinColumns = @JoinColumn(name = "calendarEventId"),
        inverseJoinColumns = @JoinColumn(name = "tagId")
    )
    private List<CalendarTag> tags;

    @Column(name = "createdAt")
    private Date createdAt;
    
    /**
     * The time of the event.
     * If isAllDay is true, set this to any time in a specific day.
     *  */ 
    @Column(name = "eventTime", nullable = false)
    private Date eventTime;

    @Column(name = "isAllDay", nullable = false)
    @Builder.Default
    private boolean isAllDay = false;

    /**Duration in minutes */
    @Column(name = "duration")
    @Min(0)
    @Max(60 * 24)
    private Integer duration;
    
    
    @PrePersist
    void createdAtUpdate() {
    this.createdAt = new Date();
    }
    
}
