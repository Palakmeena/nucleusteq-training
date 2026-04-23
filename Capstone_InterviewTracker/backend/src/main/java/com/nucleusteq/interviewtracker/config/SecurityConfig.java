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
 *
 * This class wires everything together — it tells Spring Security:
 * - Which endpoints are public and which require authentication
 * - Which roles can access which endpoints
 * - To use JWT instead of sessions (stateless)
 * - To run our JwtFilter before the default login filter
 * - How to encode and verify passwords
 *
 * @EnableMethodSecurity allows us to use @PreAuthorize on individual
 *                       controller methods later for fine-grained role checks.
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
     * BCrypt is the industry standard — it automatically handles salting
     * and is intentionally slow to make brute force attacks harder.
     *
     * We declare this as a @Bean so it can be injected anywhere
     * (like AuthService) without creating a new instance each time.
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
     * DaoAuthenticationProvider is the standard provider for
     * database-backed authentication. It uses our UserDetailsService
     * to load the user and our PasswordEncoder to verify the password.
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
     * AuthService needs this to actually trigger the authentication
     * process (verify email + password) during login.
     *
     * @param config Spring's authentication configuration
     * @return the AuthenticationManager
     * @throws Exception if the manager cannot be built
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * The main security filter chain — this is where we define
     * all the HTTP security rules for the application.
     *
     * Key decisions made here:
     * - CSRF disabled because we're a stateless REST API using JWT,
     * not a browser-based app with sessions and cookies
     * - Sessions set to STATELESS — we never create or use HTTP sessions,
     * every request must carry its own JWT token
     * - Public routes: login and panel activation link
     * - Everything else requires authentication
     *
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
                 * CSRF attacks target browser session cookies, which we don't use.
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
                 * This means Spring Security will never create an HttpSession.
                 * Every request is independent and must carry a valid JWT.
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