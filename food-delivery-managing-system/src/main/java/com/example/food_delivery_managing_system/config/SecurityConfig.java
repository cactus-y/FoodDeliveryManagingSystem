package com.example.food_delivery_managing_system.config;

import com.example.food_delivery_managing_system.user.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    // ===== 관리자 페이지 로그인 filterchain
    private final AdminAuthenticationSuccessHandler adminSuccessHandler;
    private final AdminAuthenticationFailureHandler adminFailureHandler;

    public SecurityConfig(AdminAuthenticationSuccessHandler adminSuccessHandler,
                          AdminAuthenticationFailureHandler adminFailureHandler) {
        this.adminSuccessHandler = adminSuccessHandler;
        this.adminFailureHandler = adminFailureHandler;
    }

    @Bean
    @Order(1)  // Admin 필터 먼저 실행
    public SecurityFilterChain adminFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/admin/**");
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/admin/login")
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/login")
                        .permitAll()
                        .anyRequest().hasRole("ADMIN")  // 다른 기능은 ADMIN 역할 필요
                )
                .formLogin(form -> form
                        .loginPage("/admin/login")
                        .loginProcessingUrl("/admin/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler(adminSuccessHandler)
                        .failureHandler(adminFailureHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/admin/logout") // 로그아웃을 처리할 URL (POST 요청)
                        .logoutSuccessUrl("/admin/login?logout") // 로그아웃 성공 후 이동할 페이지
                        .invalidateHttpSession(true) // 세션 무효화
                        .deleteCookies("JSESSIONID") // JSESSIONID 쿠키 삭제
                );

        return http.build();
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
                                , "/image/*"
                                , "/user/css/*"
                                , "/user/js/*").permitAll()
                        .requestMatchers("/ws-stomp", "/pub/**", "/sub/**").authenticated()
                        .requestMatchers(
                                "/api/admin/**"
                                , "/api/users/*/status"
                                , "/api/restaurants/*/*/status"
                                , "/api/statistics"
                        )
                        .hasRole("ADMIN")  // ADMIN만 접근
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
