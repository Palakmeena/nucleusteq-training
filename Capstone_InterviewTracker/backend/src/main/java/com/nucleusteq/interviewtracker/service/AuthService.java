package com.nucleusteq.interviewtracker.service;

import com.nucleusteq.interviewtracker.dto.LoginRequestDto;
import com.nucleusteq.interviewtracker.dto.LoginResponseDto;
import com.nucleusteq.interviewtracker.dto.SignupRequestDto;
import com.nucleusteq.interviewtracker.entity.CandidateProfile;
import com.nucleusteq.interviewtracker.entity.User;
import com.nucleusteq.interviewtracker.mapper.AuthMapper;
import com.nucleusteq.interviewtracker.repository.CandidateProfileRepository;
import com.nucleusteq.interviewtracker.repository.UserRepository;
import com.nucleusteq.interviewtracker.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import com.nucleusteq.interviewtracker.exception.BusinessException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service class that handles business logic for authentication.
 */
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final CandidateProfileRepository candidateProfileRepository;
    private final JwtUtil jwtUtil;
    private final AuthMapper authMapper;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    @Value("${app.frontend.base-url:http://localhost:5500}")
    private String frontendBaseUrl;

    /**
     * Constructor injection for required dependencies.
     */
    @Autowired
    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       CandidateProfileRepository candidateProfileRepository,
                       JwtUtil jwtUtil,
                       AuthMapper authMapper,
                       JavaMailSender mailSender) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.candidateProfileRepository = candidateProfileRepository;
        this.jwtUtil = jwtUtil;
        this.authMapper = authMapper;
        this.mailSender = mailSender;
    }

    /**
     * Handles the login process end to end.
     *
     * @param request the login request DTO
     * @return the login response DTO
     */
    public LoginResponseDto login(LoginRequestDto request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (DisabledException e) {
            throw new DisabledException(
                    "Your account has not been activated yet. "
                    + "Please check your email for the activation link."
            );
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(
                    "Invalid email or password. Please try again."
            );
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(
                        "User not found after authentication — this should never happen."
                ));

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        return authMapper.mapToLoginResponse(user, token);
    }

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AuthService.class);

    @Transactional
    public LoginResponseDto signup(SignupRequestDto request,
                                   org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        java.util.Optional<User> existingUser = userRepository.findByEmail(request.getEmail());

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            if (user.isActive()) {
                throw new BusinessException("Email already exists and is already verified.");
            } else {
                // User exists but not active - resend activation email and refresh profile.
                String newToken = UUID.randomUUID().toString();
                user.setActivationToken(newToken);
                user.setTokenExpiry(LocalDateTime.now().plusHours(24));
                user.setFullName(request.getFullName());
                userRepository.save(user);

                CandidateProfile profile = candidateProfileRepository.findByUser(user)
                        .orElse(new CandidateProfile(user));
                profile.setEmail(user.getEmail());
                profile.setFullName(request.getFullName());
                profile.setMobileCode(request.getMobileCode());
                profile.setMobileNumber(request.getMobileNumber());
                profile.setDateOfBirth(request.getDateOfBirth());
                profile.setGender(request.getGender());
                candidateProfileRepository.save(profile);

                sendActivationEmail(user.getEmail(), user.getFullName(), newToken);
                return authMapper.mapToLoginResponse(user, null);
            }
        }

        String activationToken = UUID.randomUUID().toString();
        LocalDateTime tokenExpiry = LocalDateTime.now().plusHours(24);
        String tempPassword = UUID.randomUUID().toString();

        User user = new User(
                request.getFullName(),
                request.getEmail(),
                passwordEncoder.encode(tempPassword),
                com.nucleusteq.interviewtracker.enums.UserRole.CANDIDATE
        );
        user.setActive(false);
        user.setActivationToken(activationToken);
        user.setTokenExpiry(tokenExpiry);
        userRepository.save(user);

        CandidateProfile profile = new CandidateProfile(user);
        profile.setEmail(user.getEmail());
        profile.setFullName(request.getFullName());
        profile.setMobileCode(request.getMobileCode());
        profile.setMobileNumber(request.getMobileNumber());
        profile.setDateOfBirth(request.getDateOfBirth());
        profile.setGender(request.getGender());
        candidateProfileRepository.save(profile);

        sendActivationEmail(user.getEmail(), user.getFullName(), activationToken);

        return authMapper.mapToLoginResponse(user, null);
    }

    private void sendActivationEmail(String toEmail, String fullName, String token) {
        try {
            String base = frontendBaseUrl == null ? "http://localhost:5500" : frontendBaseUrl.trim();
            if (base.endsWith("/")) base = base.substring(0, base.length() - 1);
            String link = base + "/pages/auth/activate.html?token=" + token;

            SimpleMailMessage message = new SimpleMailMessage();
            if (fromEmail != null && !fromEmail.isBlank()) message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Activate your HireTrack account");
            message.setText("Hi " + fullName + ",\n\n"
                    + "Thanks for signing up for HireTrack!\n"
                    + "Please set your password by clicking the link below:\n"
                    + link + "\n\n"
                    + "This link will expire in 24 hours.\n\n"
                    + "Best regards,\nHireTrack Team");
            mailSender.send(message);
            logger.info("Activation email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            logger.error("CRITICAL: Failed to send activation email to {}: {}", toEmail, e.getMessage());
        }
    }

    @Transactional
    public void verifyCandidate(String token) {
        User user = userRepository.findByActivationToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid verification token"));

        if (user.getTokenExpiry() == null || LocalDateTime.now().isAfter(user.getTokenExpiry())) {
            throw new BusinessException("Verification token has expired.");
        }

        user.setActive(true);
        user.setActivationToken(null);
        user.setTokenExpiry(null);
        userRepository.save(user);
    }
}