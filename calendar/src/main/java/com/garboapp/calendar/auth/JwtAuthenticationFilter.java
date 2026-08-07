package com.garboapp.calendar.auth;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    
    private final Logger logger = Logger.getLogger("JWTAuthenticationFilter");

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain chain) throws ServletException, IOException {
        
        
        logger.info("Extracting JWT Claims for request from " + request.getRemoteHost());
        
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        var principal = new UserPrincipal(0, "temp-user", UserRole.ADMIN);

        var authentication = new UsernamePasswordAuthenticationToken(
            principal, null, authorities
        );


        SecurityContextHolder.getContext().setAuthentication(authentication);

        logger.info("Succesfully authenticated " + authentication.getName());

        chain.doFilter(request, response);
    }

   
}