package com.garboapp.calendar;

import java.util.logging.Logger;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.garboapp.calendar.auth.UserPrincipal;

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
public class HelloController {
    private static final Logger logger = Logger.getLogger("HelloController");

    @GetMapping("/api/v1/public/{whatever}")
    public DefaultResponse<Object> sayHello(@PathVariable String whatever) {
        logger.info("Testing!");
        return DefaultResponse.builder()
                .ok(true)
                .content(whatever)
                .build();
    }
    @GetMapping("/api/v1/private/{whatever}")
    public DefaultResponse<Object> sayHelloPrivate(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable String whatever) {
        logger.info(""+userPrincipal.userId());
        return DefaultResponse.builder()
                .ok(true)
                .content(whatever)
                .build();
    }

}
