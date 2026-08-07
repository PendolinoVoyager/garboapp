package com.garboapp.calendar.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.garboapp.calendar.auth.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig{
	
	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;
	// org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(c -> c.disable())
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			// .authenticationProvider(authenticationProvider)
			.authorizeHttpRequests(auth -> auth
				// Public Endpoints
				.requestMatchers("/api/*/public/**").permitAll()
				// Logged in endpoints
				.requestMatchers("/api/*/private/**").authenticated()
				.requestMatchers("/actuator/health", "/actuator/info").permitAll()
				.requestMatchers("/actuator/**").hasAnyAuthority("ROLE_ADMIN")
				// Rest just in case
				.anyRequest().denyAll()
			)
			.addFilterAfter(jwtAuthenticationFilter, BasicAuthenticationFilter.class);
		return http.build();
	}


	
}