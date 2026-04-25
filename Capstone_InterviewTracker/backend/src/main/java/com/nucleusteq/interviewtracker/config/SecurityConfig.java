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

                        // panel activation link sent via email — must be public
                        .requestMatchers("/auth/activate").permitAll()

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

                .authenticationProvider(authenticationProvider())

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();
        configuration.addAllowedOriginPattern("*"); // Allow all origins for local testing (e.g., http://127.0.0.1:5500)
        configuration.addAllowedMethod("*"); // Allow all HTTP methods (GET, POST, PUT, DELETE, OPTIONS)
        configuration.addAllowedHeader("*"); // Allow all headers
        configuration.setAllowCredentials(true); // Allow credentials (like Authorization headers)
        
        org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}