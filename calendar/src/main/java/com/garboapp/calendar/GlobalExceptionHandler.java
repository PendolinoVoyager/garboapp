package com.garboapp.calendar;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


import org.apache.coyote.BadRequestException;
import org.springframework.boot.json.JsonParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.garboapp.calendar.auth.JwtTokenExpiredException;
import com.garboapp.calendar.utils.NotOkResponse;
import com.garboapp.calendar.utils.NotOkResponseReasonCode;



@RestController
@RestControllerAdvice
public class GlobalExceptionHandler  {
    
    private static final Logger logger = Logger.getGlobal();
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



    @ExceptionHandler({
        JsonParseException.class,
        BadRequestException.class,
        HttpMessageNotReadableException.class,
        MissingServletRequestParameterException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleBadRequestBody(Exception ex) {
        return ResponseEntity.badRequest()
            .body(NotOkResponse.builder()
                .reasonCode(NotOkResponseReasonCode.NOT_SPECIFIED)
                .message(ex.getMessage())
                .build()
            );
    
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleValidationErrors(MethodArgumentNotValidException ex) {
        logger.warning(ex.toString());
        return ResponseEntity.badRequest()
            .body(NotOkResponse.builder()
                .reasonCode(NotOkResponseReasonCode.NOT_SPECIFIED)
                .message(Map.<String, Object>of("validationErrors", ex.getAllErrors(), "message", ex.getMessage()))
                .build()
            );
    }
    @ExceptionHandler(HandlerMethodValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleMethodValidationErrors(HandlerMethodValidationException ex) {
        logger.warning(ex.toString());
        List<String> errors = ex.getParameterValidationResults()
                    .stream()
                    .<String>map(e -> e.getMethodParameter().toString() + ": " + 
                                    Arrays.toString(e.getResolvableErrors().stream().map(err -> err.getDefaultMessage()).toArray()))
                    .toList();
        return ResponseEntity.badRequest()
            .body(NotOkResponse.builder()
                .reasonCode(NotOkResponseReasonCode.NOT_SPECIFIED)
                .message(Map.<String, Object>of("errors", errors, "message", ex.getMessage()))
                .build()
            );
    }


    @ExceptionHandler
    public ResponseEntity<NotOkResponse> handleInternalServerError(Exception ex) {
        logger.severe("Exception occured:\n" +ex.getMessage());
        ex.printStackTrace();
       
        var res = ResponseEntity.status(500).body(NotOkResponse.builder()
             .reasonCode(NotOkResponseReasonCode.UNKNOWN_ERROR)
             .message(ex.getMessage())
             .build());
        return res;
    }

    
            
}
