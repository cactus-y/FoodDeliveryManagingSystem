package com.example.food_delivery_managing_system.user.service;

import com.example.food_delivery_managing_system.user.entity.CustomUserDetails;
import com.example.food_delivery_managing_system.user.entity.User;
import com.example.food_delivery_managing_system.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("No user with email: " + username));
        return new CustomUserDetails(user);
    }
}
