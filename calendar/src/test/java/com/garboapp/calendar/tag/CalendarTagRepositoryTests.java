package com.garboapp.calendar.tag;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.garboapp.calendar.calendar_tag.CalendarTag;
import com.garboapp.calendar.calendar_tag.CalendarTagRepository;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class CalendarTagRepositoryTests {
    
    @Autowired
    private CalendarTagRepository calendarTagRepository;
    
    private static final String DEFAULT_OK_TAG_NAME = "Some tag!";
    private static final String TAG_TOO_LONG = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
    
    
    @Test
    public void testFindByTagName() {
        CalendarTag tag = CalendarTag.builder().name(DEFAULT_OK_TAG_NAME).build();
        calendarTagRepository.save(tag);
        calendarTagRepository.flush();
        assert(calendarTagRepository.findByName(DEFAULT_OK_TAG_NAME).get().getName().equals(tag.getName()));
    }

    @Test
    public void testExceptionOnTagTooLong() {
        var tag = CalendarTag.builder().name(TAG_TOO_LONG).build();
        assertThrows(Exception.class, () -> {
            calendarTagRepository.save(tag);
            calendarTagRepository.flush();
        });
    }
    @Test
    public void testOnlyUniqueTagNames() {
        CalendarTag tag1 = CalendarTag.builder().name(DEFAULT_OK_TAG_NAME).build();
        CalendarTag tag2 = CalendarTag.builder().name(DEFAULT_OK_TAG_NAME).build();
        assertThrows(Exception.class, () -> {
            calendarTagRepository.save(tag1);
            calendarTagRepository.flush();
            calendarTagRepository.save(tag2);
            calendarTagRepository.flush();
        });
    }

}
