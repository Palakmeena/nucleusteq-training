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
 *
 * Spring Security doesn't know about our database — it just works with
 * a UserDetails object. This class is the bridge between the two.
 * When someone tries to log in, Spring Security calls loadUserByUsername()
 * here, we fetch the user from our DB and return it in a format
 * Spring Security understands.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Constructor injection is preferred over @Autowired on fields —
     * it makes dependencies explicit and easier to test.
     *
     * @param userRepository repository used to fetch user from database
     */
    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads a user from the database using their email address.
     * Spring Security calls this method automatically during authentication.
     *
     * We also wrap the user's role as a GrantedAuthority — this is how
     * Spring Security understands permissions (e.g. ROLE_HR, ROLE_CANDIDATE).
     *
     * @param email the email address used as the login username
     * @return UserDetails object containing credentials and authorities
     * @throws UsernameNotFoundException if no user exists with this email
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