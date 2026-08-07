package com.garboapp.calendar.auth;
/**
 * 
 * UserPrincipal
 * Contains data extracted from a JWT token.
 * 
 * @param username
 * @param role
 */
public record UserPrincipal(int userId, String username, UserRole userRole) {}
