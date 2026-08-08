package com.garboapp.calendar;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.garboapp.calendar.auth.JwtTokenExpiredException;

@RestController
@RequestMapping("/api/v1/public")
public class TestController {
    @GetMapping("/throw")
    public void throwGenericError() throws Exception {
        throw new Exception("Reqular exception!");
    }

    @GetMapping("/throwjwt")
    public void throwJwtError() throws JwtTokenExpiredException {
        throw new JwtTokenExpiredException("Oopsie!");
    }
}