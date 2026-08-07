package com.garboapp.calendar;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class DefaultResponse<T> {
    private boolean ok;
    private T content;
}

@RestController
@RequestMapping("/api/v1/public")
public class HelloController {
    private static final Logger logger = Logger.getLogger("HelloController");
    @GetMapping("/{whatever}")
    public DefaultResponse<Object> sayHello(@PathVariable String whatever) {
        logger.info("Testing!");
        return DefaultResponse.builder()
                .ok(true)
                .content(whatever)
                .build();
    }

}
