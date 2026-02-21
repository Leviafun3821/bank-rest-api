package com.example.bankcards.config;

import com.example.bankcards.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Открытые пути — без авторизации
                        .requestMatchers(
                                "/api/auth/login",                    // наш логин
                                "/swagger-ui/**",                     // Swagger UI страницы
                                "/swagger-ui.html",                   // прямой доступ
                                "/v3/api-docs/**",                    // OpenAPI JSON
                                "/api-docs/**",                       // старый путь (на всякий)
                                "/swagger-resources/**",              // ресурсы
                                "/webjars/**",                        // JS/CSS для UI
                                "/swagger-resources/configuration/ui", // конфиг UI
                                "/swagger-resources/configuration/security" // конфиг security
                        ).permitAll()
                        // Админские эндпоинты
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // Пользовательские эндпоинты
                        .requestMatchers("/api/cards/my/**", "/api/transfers/**").hasRole("USER")
                        // Всё остальное — только авторизованные
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}
