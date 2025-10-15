package com.example.food_delivery_managing_system.config;

import com.example.food_delivery_managing_system.user.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity

public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(CustomUserDetailsService userDetailsService,
        PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/auth/login", "/api/auth/logout", "/api/**")
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/login"
                    , "/api/auth/logout"
                    , "/login"
                    , "/user/user_signup"
                    , "/signup"
                    , "/api/users/check-email"
                    , "/api/users/nick-name"
                    , "/user/image/*"
                    , "/user/css/*"
                    , "/user/js/*").permitAll()
                .requestMatchers("/ws-stomp", "/pub/**", "/sub/**").authenticated()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")           // 커스텀 로그인 페이지 경로
                .usernameParameter("email")         // "email" 필드 사용
                .passwordParameter("password")      // "password" 필드 사용
                .defaultSuccessUrl("/restaurants", true)   // 성공 후 이동할 경로
                .permitAll()
            )
            .sessionManagement(sm -> sm.sessionFixation(sess -> sess.migrateSession()));

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}