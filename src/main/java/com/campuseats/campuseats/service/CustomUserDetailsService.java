package com.campuseats.campuseats.service;

import com.campuseats.campuseats.model.User;
import com.campuseats.campuseats.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority; // Import this
import java.util.Collections;
import java.util.List; // Import List
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String collegeId) throws UsernameNotFoundException {
        User user = userRepository.findById(collegeId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // FIX: Load the role from the database
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(user.getRole())
        );

        return new org.springframework.security.core.userdetails.User(
                user.getCollegeId(),
                user.getPassword(),
                authorities
        );
    }
}