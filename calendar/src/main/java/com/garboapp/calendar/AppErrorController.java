package com.garboapp.calendar;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.garboapp.calendar.auth.JwtTokenExpiredException;
import com.garboapp.calendar.utils.NotOkResponse;
import com.garboapp.calendar.utils.NotOkResponseReasonCode;


@RestController
@RestControllerAdvice
public class AppErrorController  {
    
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<NotOkResponse> handleNotFoundError(Exception ex) {
        var res = ResponseEntity.status(404).body(NotOkResponse.builder()
             .reasonCode(NotOkResponseReasonCode.NOT_SPECIFIED)
             .message(ex.getMessage())
             .build());
             return res;
    }   

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(JwtTokenExpiredException.class)
    public ResponseEntity<NotOkResponse> handleExpiredTokenException(JwtTokenExpiredException ex) {
        var res = ResponseEntity.status(401).body(NotOkResponse.builder()
        .reasonCode(NotOkResponseReasonCode.TOKEN_EXPIRED)
        .message(ex.getMessage())
        .build());
        return res;
    }

    @ExceptionHandler
    public ResponseEntity<NotOkResponse> handleInternalServerError(Exception ex) {
        var res = ResponseEntity.status(500).body(NotOkResponse.builder()
             .reasonCode(NotOkResponseReasonCode.UNKNOWN_ERROR)
             .message(ex.getMessage())
             .build());
        return res;
    }
            
}
