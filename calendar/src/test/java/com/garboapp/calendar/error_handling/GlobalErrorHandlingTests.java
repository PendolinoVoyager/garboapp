package com.garboapp.calendar.error_handling;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import com.garboapp.calendar.GlobalExceptionHandler;
import com.garboapp.calendar.auth.JwtTokenExpiredException;
import com.garboapp.calendar.config.SecurityConfig;
import com.garboapp.calendar.utils.NotOkResponse;
import com.garboapp.calendar.utils.NotOkResponseReasonCode;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Import(SecurityConfig.class) // Important as Spring Security will slap authorized on all endpoints
@WebMvcTest(GlobalExceptionHandler.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Mock
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    
    @Test
    @DisplayName("NoResourceFoundException -> 404 with NOT_SPECIFIED reason code")
    void handlesNotFoundError() throws Exception {
        mockMvc.perform(get("/definitely_not_a_path1232143"))
        .andExpect(status().is(404))
        .andExpect(jsonPath("$.message").exists())
        .andExpect(jsonPath("$.reasonCode").value(NotOkResponseReasonCode.NOT_SPECIFIED));
    }

    // Can't bother generate an expired token so...
    @Test
    @DisplayName("JwtTokenExpiredException -> 401 with TOKEN_EXPIRED reason code")
    void handlesExpiredTokenException() {
        var ex = new JwtTokenExpiredException("token expired at 2026-08-08T10:00:00Z");

        ResponseEntity<NotOkResponse> response = handler.handleExpiredTokenException(ex);

        assert(response.getStatusCode() == HttpStatus.UNAUTHORIZED);
        assert(response.getBody() != null);
        assert(response.getBody().getReasonCode() == NotOkResponseReasonCode.TOKEN_EXPIRED);
        assert(response.getBody().getMessage().equals(ex.getMessage()));
    }

    @Test
    @DisplayName("Generic Exception -> 500 with UNKNOWN_ERROR reason code")
    void handlesInternalServerError() {
        var ex = new RuntimeException("something blew up");

        ResponseEntity<NotOkResponse> response = handler.handleInternalServerError(ex);

        assert(response.getStatusCode() == (HttpStatus.INTERNAL_SERVER_ERROR));
        assert(response.getBody() != null);
        assert(response.getBody().getReasonCode() == NotOkResponseReasonCode.UNKNOWN_ERROR);
        assert(response.getBody().getMessage().equals(ex.getMessage()));
    }

    @Test
    @DisplayName("Handles null exception message gracefully")
    void handlesNullMessage() {
        var ex = new RuntimeException();

        ResponseEntity<NotOkResponse> response = handler.handleInternalServerError(ex);

        assert(response.getBody().getMessage() == null);
    }


}