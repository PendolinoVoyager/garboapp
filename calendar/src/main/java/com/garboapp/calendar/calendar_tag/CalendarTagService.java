package com.garboapp.calendar.calendar_tag;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import javassist.NotFoundException;

@Service
public class CalendarTagService {
    
    @Autowired
    private CalendarTagRepository tagRepository;

    private final static Logger logger = Logger.getLogger(CalendarTag.class.getName());

    public Optional<String> getNameById(Integer id) {
        return tagRepository.findById(id).map(t -> t.getName());
    }

    @Transactional
    public void deleteTag(Integer tagId) {
        tagRepository.deleteById(tagId);
        tagRepository.flush();
        logger.info("Deleted tag: " + tagId);

    }

    @Transactional
    public CalendarTag createTag(String name) throws SQLIntegrityConstraintViolationException {
        if (tagRepository.findByName(name).isPresent()) {
            var ex = new SQLIntegrityConstraintViolationException("Tag" + name + " already exists");
            throw ex;
        }
        var newTag = CalendarTag.builder().name(name).build();
        tagRepository.save(newTag);
        tagRepository.flush();
        logger.info("Added new tag: " + name);
        return newTag;
    }

    /**
     * Get CalendarTags in a matching size list of names.
     * If a tag doesn't exist, it's mapped as Optional.empty.
     * @param names
     * @return
     */
    public List<Optional<CalendarTag>> findAllByNames(List<String> names) {
        return names.stream().<Optional<CalendarTag>>map(name -> {
            return tagRepository.findByName(name);
        }).toList();
    }

    public CalendarTag findByName(String name) throws NotFoundException {
        return tagRepository.findByName(name).orElseThrow(() -> new NotFoundException("e"));
    }

    public List<CalendarTag> findOrCreateTagsByNames(List<String> names) {
        return names.stream().<CalendarTag>map(name -> {
             var tag = tagRepository.findByName(name);
             if (tag.isPresent()) {
                return tag.get();
             }
            return tagRepository.saveAndFlush(
                CalendarTag.builder().name(name).build());
        }).collect(Collectors.toList());
    }


}
