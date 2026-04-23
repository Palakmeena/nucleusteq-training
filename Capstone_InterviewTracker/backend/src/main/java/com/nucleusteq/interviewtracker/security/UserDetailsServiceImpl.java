package com.nucleusteq.interviewtracker.security;

import com.nucleusteq.interviewtracker.entity.User;
import com.nucleusteq.interviewtracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Custom implementation of Spring Security's UserDetailsService.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Constructor injection is preferred over @Autowired on fields —
     * it makes dependencies explicit and easier to test.
     */
    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads a user from the database using their email address.
     * Spring Security calls this method automatically during authentication.
     */
    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No user found with email: " + email
                ));

        /*
         * We prefix the role with "ROLE_" because Spring Security expects
         * authorities in this format when using hasRole() checks.
         * So HR becomes ROLE_HR, CANDIDATE becomes ROLE_CANDIDATE, etc.
         */
        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.isActive(),   // account enabled only if HR has activated it
                true,              // account not expired
                true,              // credentials not expired
                true,              // account not locked
                List.of(authority)
        );
    }
}