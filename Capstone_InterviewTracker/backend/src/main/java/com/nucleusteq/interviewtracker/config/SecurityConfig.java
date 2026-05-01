package com.nucleusteq.interviewtracker.config;

import com.nucleusteq.interviewtracker.security.JwtFilter;
import com.nucleusteq.interviewtracker.security.UserDetailsServiceImpl;
import com.nucleusteq.interviewtracker.util.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Central security configuration for the application.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * Constructor injection — keeps dependencies clear and testable.
     *
     * @param jwtFilter          our custom JWT filter
     * @param userDetailsService loads users from database for authentication
     */
    @Autowired
    public SecurityConfig(final JwtFilter jwtFilter,
            final UserDetailsServiceImpl userDetailsService) {
        this.jwtFilter = jwtFilter;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Defines the password encoder used throughout the application.
     *
     * @return BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Sets up the authentication provider that Spring Security uses
     * to verify login credentials.
     *
     * @return configured DaoAuthenticationProvider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Exposes the AuthenticationManager as a bean.
     *
     * @param config Spring's authentication configuration
     * @return the AuthenticationManager
     * @throws Exception if the manager cannot be built
     */
    @Bean
    public AuthenticationManager authenticationManager(
            final AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * The main security filter chain — defines all HTTP security rules.
     *
     * @param http the HttpSecurity builder provided by Spring
     * @return the built SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http)
            throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        // Allow CORS preflight requests
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()

                        // login is public — no token needed to log in
                        .requestMatchers("/auth/login").permitAll()
                        
                        // signup is public
                        .requestMatchers("/auth/signup").permitAll()

                        // panel activation link sent via email — must be public
                        .requestMatchers("/auth/activate").permitAll()

                        // candidate verification link sent via email — must be public
                        .requestMatchers("/auth/verify-candidate").permitAll()
                        
                        // Allow Spring Boot error endpoint so validation errors
                        // don't get transformed into security 401/403 responses.
                        .requestMatchers("/error").permitAll()

                        // public JD listing — candidates browse before logging in
                        .requestMatchers("/jd/**").permitAll()

                        /*
                         * Candidate self-registration is public —
                         * they don't have an account yet when filling this form.
                         */
                        .requestMatchers("/candidate/register").permitAll()

                        /*
                         * Resume upload is public so candidate can upload
                         * right after registering without needing to log in first.
                         */
                        .requestMatchers("/candidate/resume/**").permitAll()

                        // HR-only endpoints
                        .requestMatchers("/hr/**").hasRole("HR")

                        // panel endpoints
                        .requestMatchers("/panel/**").hasRole("PANEL")

                        // candidate endpoints — requires CANDIDATE role
                        .requestMatchers("/candidate/**").hasRole("CANDIDATE")

                        // everything else requires authentication
                        .anyRequest().authenticated()
                )

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            writeErrorResponse(
                                    response,
                                    HttpStatus.UNAUTHORIZED,
                                    "Unauthorized request. Please login first."
                            );
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            writeErrorResponse(
                                    response,
                                    HttpStatus.FORBIDDEN,
                                    "Access denied for this endpoint."
                            );
                        })
                )

                .authenticationProvider(authenticationProvider())

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();
        configuration.setAllowedOrigins(java.util.List.of(
                "http://127.0.0.1:5500",
                "http://localhost:5500",
                "http://127.0.0.1:5501",
                "http://localhost:5501",
                "http://127.0.0.1:5502",
                "http://localhost:5502",
                "http://127.0.0.1:5504",
                "http://localhost:5504"
        ));
        configuration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(java.util.List.of("*"));
        configuration.setAllowCredentials(true);

        org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private void writeErrorResponse(
            final HttpServletResponse response,
            final HttpStatus status,
            final String message) throws java.io.IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        ApiResponse<Object> body = ApiResponse.error(message);
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
    }
}