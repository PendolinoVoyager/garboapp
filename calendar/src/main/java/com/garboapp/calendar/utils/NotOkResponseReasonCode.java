package com.garboapp.calendar.utils;

/** 
 * This class contains static values for error codes for when an error is returned.
 * For example 401 with TOKEN_EXPIRED 
 * may signal the app to try and refresh the token before showing any errors. 
 * */ 
public abstract class NotOkResponseReasonCode {
    public static final int NOT_SPECIFIED = 0;
    public static final int BAD_TOKEN     = 1;
    public static final int TOKEN_EXPIRED = 2;
    public static final int UNKNOWN_ERROR = 3;
}
