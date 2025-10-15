package com.example.food_delivery_managing_system.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
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
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))

                .authorizeHttpRequests(auth -> auth
                        // 정적 리소스( /css/**, /js/**, /images/**, webjars 등 ) 전부 허용
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers(
                                "/favicon.ico",
                                "/assets/**",
                                "/css/**", "/js/**", "/images/**",
                                "/user/css/**", "/user/js/**", "/user/images/**"
                        ).permitAll()
                        // 공개 페이지/엔드포인트
                        .requestMatchers(
                                "/", "/login", "/logout", "/error",
                                "/user/user_signup",
                                "/api/users/signup", "/api/users/check-email", "/api/users/nick-name",
                                "/actuator/health"
                        ).permitAll()

                        // (옵션) 이미지 업로드를 로그인 없이 테스트하려면 아래 허용
                        //.requestMatchers("/api/files/upload").permitAll()
                        .anyRequest().authenticated()
                )

                // 폼 로그인
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        // "/" 가 공개이므로 로그인 직후 여기로 이동해도 루프 안 걸림
                        .defaultSuccessUrl("/users/me", true)
                        .permitAll()
                )

                // 로그아웃
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .logoutSuccessUrl("/login?logout")

                )
                // 세션 고정 보호
                .sessionManagement(sm -> sm.sessionFixation(sess -> sess.migrateSession()))
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
