package com.pm.farm_backend.Security.config;

import com.pm.farm_backend.Security.filter.JwtAuthenticationFilter;
import com.pm.farm_backend.Security.Service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

        @Autowired
        private JwtAuthenticationFilter jwtAuthenticationFilter;

        @Autowired
        private CustomUserDetailsService userDetailsService;

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

                http
                                .csrf(csrf -> csrf.disable())
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(
                                                                "/swagger-ui/**",
                                                                "/v3/api-docs/**",
                                                                "/swagger-ui.html",
                                                                "/api/buyer/products/**",
                                                                "/api/buyer/products")
                                                .permitAll()
                                                .requestMatchers("/api/auth/**").permitAll()
                                                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                                                .requestMatchers("/api/buyer/**").hasRole("BUYER")
                                                .requestMatchers("/api/dashboard/**").hasRole("FARMER")
                                                .requestMatchers("/api/task/**").hasRole("FARMER")
                                                .requestMatchers("/api/tasks/**").hasRole("FARMER")
                                                .requestMatchers("/api/crop-cycle/**").hasRole("FARMER")
                                                .requestMatchers("/api/crop-cycles/**").hasRole("FARMER")
                                                .requestMatchers("/api/subtasks/**").hasRole("FARMER")
                                                .requestMatchers("/api/farmer/**").hasRole("FARMER")
                                                .requestMatchers("/api/finance/**").hasRole("FARMER")
                                                .requestMatchers("/api/weather/**").authenticated()
                                                .requestMatchers("/api/notifications/**").authenticated()
                                                .anyRequest().authenticated())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authenticationProvider(authenticationProvider())
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public AuthenticationProvider authenticationProvider() {
                DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
                authProvider.setUserDetailsService(userDetailsService);
                authProvider.setPasswordEncoder(passwordEncoder());
                return authProvider;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();

                // Use allowedOriginPatterns instead of allowedOrigins when allowCredentials is
                // true
                configuration.setAllowedOriginPatterns(Arrays.asList(
                                "http://localhost:5173", // Vite dev server
                                "http://localhost:3000", // Alternative React dev server
                                "http://localhost:5174", // Alternative Vite port
                                "http://localhost", // Nginx proxy (port 80)
                                "http://localhost:*", // Any localhost port
                                "http://127.0.0.1:*" // Localhost IP with any port
                ));

                // Allow all HTTP methods
                configuration.setAllowedMethods(Arrays.asList(
                                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

                // Allow all headers
                configuration.setAllowedHeaders(Arrays.asList(
                                "Authorization",
                                "Content-Type",
                                "Accept",
                                "Origin",
                                "X-Requested-With"));

                // Expose headers to frontend
                configuration.setExposedHeaders(Arrays.asList(
                                "Authorization"));

                // Allow credentials (cookies, authorization headers)
                configuration.setAllowCredentials(true);

                // Cache preflight response for 1 hour
                configuration.setMaxAge(3600L);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }
}
