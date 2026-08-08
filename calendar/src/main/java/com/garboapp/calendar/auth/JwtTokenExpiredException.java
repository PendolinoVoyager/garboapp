package com.garboapp.calendar.auth;

public class JwtTokenExpiredException extends Exception  {
    public JwtTokenExpiredException(String message) {
        super(message);
    }
    public JwtTokenExpiredException() {
        super();
    }
}
