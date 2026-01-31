package org.example.projectfinalspring.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if ("admin".equals(username)) {
            return User.builder()
                    .username("admin")
                    .password("{bcrypt}$2a$10$")
                    .roles("ADMIN")
                    .build();
        } else if ("user".equals(username)) {
            return User.builder()
                    .username("user")
                    .password("{bcrypt}$2a$10$")
                    .roles("USER")
                    .build();
        }
        throw new UsernameNotFoundException("User not found: " + username);
    }
}