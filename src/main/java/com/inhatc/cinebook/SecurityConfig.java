package com.inhatc.cinebook;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers("/**").permitAll())
			.csrf(csrf -> csrf
				.ignoringRequestMatchers("/h2-console/**"))
			.headers(headers -> headers
				.frameOptions(frameOptions -> frameOptions.sameOrigin()))
			.formLogin(form -> form
				.loginPage("/user/signin")
				.loginProcessingUrl("/user/signin")
				.defaultSuccessUrl("/movie", true)
				.failureUrl("/user/signin?error=true"))
			.logout(logout -> logout
				.logoutUrl("/user/signout")
				.logoutSuccessUrl("/")
				.invalidateHttpSession(true));
		return http.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	AuthenticationManager authenticationManager(
			AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
}
