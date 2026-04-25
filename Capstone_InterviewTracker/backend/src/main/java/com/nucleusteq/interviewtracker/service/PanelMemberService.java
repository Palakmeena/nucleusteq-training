package com.nucleusteq.interviewtracker.service;

import com.nucleusteq.interviewtracker.dto.PanelMemberRequestDto;
import com.nucleusteq.interviewtracker.dto.PanelMemberResponseDto;
import com.nucleusteq.interviewtracker.entity.PanelMember;
import com.nucleusteq.interviewtracker.entity.User;
import com.nucleusteq.interviewtracker.enums.UserRole;
import com.nucleusteq.interviewtracker.mapper.PanelMemberMapper;
import com.nucleusteq.interviewtracker.repository.PanelMemberRepository;
import com.nucleusteq.interviewtracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for panel member management business logic.
 * Handles creation, activation and retrieval of panel members.
 */
@Service
public class PanelMemberService {

    private final PanelMemberRepository panelMemberRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PanelMemberMapper panelMemberMapper;

    /**
     * Constructor injection for all required dependencies.
     *
     * @param panelMemberRepository for panel member DB operations
     * @param userRepository        for creating linked user account
     * @param passwordEncoder       for hashing password on activation
     * @param panelMemberMapper     for entity and DTO conversions
     */
    @Autowired
    public PanelMemberService(final PanelMemberRepository panelMemberRepository,
                               final UserRepository userRepository,
                               final PasswordEncoder passwordEncoder,
                               final PanelMemberMapper panelMemberMapper) {
        this.panelMemberRepository = panelMemberRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.panelMemberMapper = panelMemberMapper;
    }

    /**
     * Creates a new panel member account by HR.
     * Generates a secure activation token and stores it on the User.
     * In a real setup this token would be emailed — for now we return
     * it in the response so you can test activation via Postman.
     *
     * @param request the panel member details from HR
     * @return the saved panel member as a response DTO
     * @throws IllegalArgumentException if email or mobile already exists
     */
    @Transactional
    public PanelMemberResponseDto createPanelMember(
            final PanelMemberRequestDto request) {

        validateNoDuplicates(request.getEmail(), request.getMobileNumber());

        /*
         * Create a User account for the panel member with a temporary
         * placeholder password. The real password is set when they
         * click the activation link and activate their account.
         * Account starts inactive — activated after password is set.
         */
        String activationToken = UUID.randomUUID().toString();
        LocalDateTime tokenExpiry = LocalDateTime.now().plusHours(24);

        User user = new User(
                request.getFullName(),
                request.getEmail(),
                passwordEncoder.encode(UUID.randomUUID().toString()),
                UserRole.PANEL
        );
        user.setActive(false);
        user.setActivationToken(activationToken);
        user.setTokenExpiry(tokenExpiry);
        User savedUser = userRepository.save(user);

        PanelMember panelMember = panelMemberMapper.mapToEntity(request);
        panelMember.setUser(savedUser);
        PanelMember saved = panelMemberRepository.save(panelMember);

        /*
         * In a real setup we would email the activation link here.
         * For now the token is logged so you can test via Postman.
         * Link format: POST /auth/activate?token=<activationToken>
         */
        return panelMemberMapper.mapToResponseDto(saved);
    }

    /**
     * Returns all panel members for HR dashboard.
     * HR needs to see all panel members to assign them to interviews.
     *
     * @return list of all panel members as response DTOs
     */
    public List<PanelMemberResponseDto> getAllPanelMembers() {
        return panelMemberRepository.findAll()
                .stream()
                .map(panelMemberMapper::mapToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Returns a single panel member by ID.
     * Used by HR to view full details of a specific panel member.
     *
     * @param id the panel member's database ID
     * @return the panel member as a response DTO
     * @throws jakarta.persistence.EntityNotFoundException if not found
     */
    public PanelMemberResponseDto getPanelMemberById(final Long id) {
        PanelMember panelMember = panelMemberRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "Panel member not found with id: " + id
                ));
        return panelMemberMapper.mapToResponseDto(panelMember);
    }

    /**
     * Activates a panel member account using the token from the email link.
     * Sets the password, marks the account as active, and clears the token.
     * Token is valid for 24 hours — expired tokens are rejected.
     *
     * @param token    the activation token from the email link
     * @param password the new password chosen by the panel member
     * @throws IllegalArgumentException if token is invalid or expired
     */
    @Transactional
    public void activatePanelMember(final String token, final String password) {

        /*
         * Find the user by activation token.
         * We search in UserRepository since the token is stored on User.
         */
        User user = userRepository.findAll()
                .stream()
                .filter(u -> token.equals(u.getActivationToken()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid activation token"
                ));

        if (user.getTokenExpiry() == null
                || LocalDateTime.now().isAfter(user.getTokenExpiry())) {
            throw new IllegalArgumentException(
                    "Activation token has expired. Please ask HR to resend the link."
            );
        }

        /*
         * Set the real password, activate the account, and clear the token
         * so it cannot be reused for security.
         */
        user.setPassword(passwordEncoder.encode(password));
        user.setActive(true);
        user.setActivationToken(null);
        user.setTokenExpiry(null);
        userRepository.save(user);

        /*
         * Also mark the PanelMember entity as active
         * so HR can see the activated status on the dashboard.
         */
        PanelMember panelMember = panelMemberRepository.findByUser(user)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "Panel member not found for this user"
                ));
        panelMember.setActive(true);
        panelMemberRepository.save(panelMember);
    }

    /**
     * Returns the profile of the currently logged-in panel member.
     * Panel member can only see their own profile.
     *
     * @param email the email of the logged-in panel member from JWT
     * @return the panel member's own profile as a response DTO
     * @throws jakarta.persistence.EntityNotFoundException if not found
     */
    public PanelMemberResponseDto getPanelMemberProfile(final String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "User not found with email: " + email
                ));

        PanelMember panelMember = panelMemberRepository.findByUser(user)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "Panel member profile not found for this user"
                ));

        return panelMemberMapper.mapToResponseDto(panelMember);
    }

    /**
     * Checks for duplicate email or mobile before creating a panel member.
     * Also checks UserRepository since email must be unique across all users.
     *
     * @param email        the email to check
     * @param mobileNumber the mobile number to check
     * @throws IllegalArgumentException if either already exists
     */
    private void validateNoDuplicates(final String email,
                                       final String mobileNumber) {
        if (panelMemberRepository.existsByEmail(email)
                || userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "An account with this email already exists"
            );
        }
        if (panelMemberRepository.existsByMobileNumber(mobileNumber)) {
            throw new IllegalArgumentException(
                    "An account with this mobile number already exists"
            );
        }
    }
}