package com.garboapp.calendar.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * Standardized response for any request that fails.
 * message is the error message that may be shown to client.
 * Reason code is NotOkResponseReasonCode.
 * NotOkResponse
 */
public class NotOkResponse {
    private Object message;
    private int reasonCode;
}
