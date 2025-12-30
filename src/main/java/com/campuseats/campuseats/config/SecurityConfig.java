package com.campuseats.campuseats.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/register", "/login", "/css/**", "/js/**", "/images/**").permitAll() // Public pages
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated() // All other pages require login
                )
                .formLogin((form) -> form
                        .loginPage("/login") // Use your custom login.html
                        .usernameParameter("collegeId") // Important: Tell Spring to look for 'collegeId' in the form, not 'username'
                        .defaultSuccessUrl("/canteens", true) // Redirect here after success
                        .permitAll()
                )
                .logout((logout) -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Use BCrypt for hashing
    }
}