package com.nucleusteq.interviewtracker.config;

import com.nucleusteq.interviewtracker.entity.User;
import com.nucleusteq.interviewtracker.enums.UserRole;
import com.nucleusteq.interviewtracker.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Seeds the database with essential default data on application startup.
 *
 * CommandLineRunner runs automatically after the Spring context is loaded
 * and all beans are initialized. We use it here to create a default HR
 * user so the application is usable immediately after first boot without
 * manually inserting data into the database.
 *
 * The seeder checks if the user already exists before creating —
 * so it is safe to restart the application multiple times without
 * getting duplicate entry errors.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor injection — both dependencies are needed to
     * create and save the default HR user.
     *
     * @param userRepository  used to check existence and save the user
     * @param passwordEncoder used to hash the password before saving
     */
    @Autowired
    public DataSeeder(UserRepository userRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Runs once on application startup.
     * Creates a default HR user if one doesn't already exist.
     * Password is BCrypt hashed — never stored as plain text.
     *
     * Default credentials (change after first login):
     * Email    : hr@interviewtracker.com
     * Password : Hr@123456
     *
     * @param args command line arguments (not used here)
     */
    @Override
    public void run(String... args) {
        createDefaultHrUser();
    }

    /**
     * Creates the default HR user if they don't already exist in the DB.
     * Logs the result so you can confirm in the console whether it
     * was created or already existed.
     */
    private void createDefaultHrUser() {
        String hrEmail = "hr@interviewtracker.com";

        if (userRepository.existsByEmail(hrEmail)) {
            logger.info("Default HR user already exists — skipping creation.");
            return;
        }

        User hrUser = new User(
                "HR Admin",
                hrEmail,
                passwordEncoder.encode("Hr@123456"),
                UserRole.HR
        );

        // HR account is active immediately — no activation needed
        hrUser.setActive(true);

        userRepository.save(hrUser);
        logger.info("Default HR user created successfully. Email: {}", hrEmail);
    }
}
