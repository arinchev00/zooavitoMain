package com.example.zooavito.config;

import com.example.zooavito.service.Security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(11);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // 👇 ДОБАВЬТЕ ЭТОТ МЕТОД
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "https://zooavito.cloudpub.ru",
                "http://localhost",
                "http://localhost:3000"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // 👈 ДОБАВЬТЕ ЭТУ СТРОКУ
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // Публичные эндпоинты
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/v1/api/auth",
                                "/v1/api/registration"
                        ).permitAll()

                        // Публичные GET запросы
                        .requestMatchers(HttpMethod.GET, "/v1/api/announcement/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v1/api/categories/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v1/api/subcategories/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v1/api/comments/**").permitAll()

                        // Админские эндпоинты категорий требуют ROLE_ADMIN
                        .requestMatchers("/v1/api/categories/admin/**").hasRole("ADMIN")

                        // Эндпоинты пользователя
                        .requestMatchers("/v1/api/user/**").authenticated()

                        // Эндпоинты с объявлениями
                        .requestMatchers(HttpMethod.POST, "/v1/api/announcement/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/v1/api/announcement/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/v1/api/announcement/**").authenticated()

                        // Эндпоинты с комментариями
                        .requestMatchers(HttpMethod.POST, "/v1/api/comments/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/v1/api/comments/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/v1/api/comments/**").authenticated()

                        // Админские эндпоинты
                        .requestMatchers("/v1/api/admin/**").hasRole("ADMIN")

                        // Категории - создание/обновление/удаление только для ADMIN
                        .requestMatchers(HttpMethod.POST, "/v1/api/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/v1/api/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/v1/api/categories/**").hasRole("ADMIN")

                        // Подкатегории - создание/обновление/удаление только для ADMIN
                        .requestMatchers(HttpMethod.POST, "/v1/api/subcategories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/v1/api/subcategories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/v1/api/subcategories/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(form -> form.disable())
                .httpBasic(httpBasic -> httpBasic.disable());

        return http.build();
    }
}