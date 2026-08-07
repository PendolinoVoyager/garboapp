package com.garboapp.calendar.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig{

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	
		http
			.csrf(Customizer.withDefaults())
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
			   // Public Endpoints
               .requestMatchers("/api/*/public/**").permitAll()
			   // Logged in endpoints
			   .requestMatchers("/api/*/private/**").authenticated()
			   // Rest just in case
               .anyRequest().denyAll()
           );
		return http.build();
	}
	
}