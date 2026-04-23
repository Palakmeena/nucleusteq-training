package com.nucleusteq.interviewtracker.config;

import com.nucleusteq.interviewtracker.security.JwtFilter;
import com.nucleusteq.interviewtracker.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    public SecurityConfig(JwtFilter jwtFilter,
            UserDetailsServiceImpl userDetailsService) {
        this.jwtFilter = jwtFilter;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Defines the password encoder used throughout the application.
     * @return BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Sets up the authentication provider that Spring Security uses
     * to verify login credentials.
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
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * The main security filter chain — this is where we define
     * all the HTTP security rules for the application.
     * @param http the HttpSecurity builder provided by Spring
     * @return the built SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                /*
                 * Disable CSRF — not needed for stateless REST APIs.
                 */
                .csrf(csrf -> csrf.disable())

                /*
                 * Define which endpoints are open and which require a valid JWT.
                 * Order matters — more specific rules should come before general ones.
                 */
                .authorizeHttpRequests(auth -> auth

                        // login is public — obviously no token needed to log in
                        .requestMatchers("/auth/login").permitAll()

                        // panel activation link is sent via email — must be public
                        .requestMatchers("/auth/activate").permitAll()

                        // public JD listing — candidates browse jobs before logging in
                        .requestMatchers("/jd/**").permitAll()

                        // HR-only endpoints — only users with ROLE_HR can access
                        .requestMatchers("/hr/**").hasRole("HR")

                        // panel endpoints — only users with ROLE_PANEL can access
                        .requestMatchers("/panel/**").hasRole("PANEL")

                        // candidate endpoints — only users with ROLE_CANDIDATE can access
                        .requestMatchers("/candidate/**").hasRole("CANDIDATE")

                        // everything else requires the user to be authenticated
                        .anyRequest().authenticated())
                /*
                 * Set session management to STATELESS.
                 */
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // plug in our custom authentication provider
                .authenticationProvider(authenticationProvider())

                /*
                 * Add our JWT filter before Spring's default
                 * UsernamePasswordAuthenticationFilter.
                 * This ensures every request is checked for a valid token
                 * before Spring tries to do its own authentication.
                 */
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}