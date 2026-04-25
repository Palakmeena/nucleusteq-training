package com.nucleusteq.interviewtracker.repository;

import com.nucleusteq.interviewtracker.entity.PanelMember;
import com.nucleusteq.interviewtracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for PanelMember entity.
 * Provides database operations for panel member management.
 */
@Repository
public interface PanelMemberRepository extends JpaRepository<PanelMember, Long> {

    /**
     * Checks if a panel member with the given email already exists.
     * Used to prevent duplicate panel member accounts.
     *
     * @param email the email to check
     * @return true if a panel member with this email exists
     */
    boolean existsByEmail(String email);

    /**
     * Checks if a panel member with the given mobile number already exists.
     * Used to prevent duplicate panel member accounts.
     *
     * @param mobileNumber the mobile number to check
     * @return true if a panel member with this number exists
     */
    boolean existsByMobileNumber(String mobileNumber);

    /**
     * Finds a panel member by their linked user account.
     * Used to load panel member details after they log in.
     *
     * @param user the user account linked to the panel member
     * @return Optional containing the panel member if found
     */
    Optional<PanelMember> findByUser(User user);

    /**
     * Finds a panel member by their email address.
     * Used during panel activation to look up the panel member.
     *
     * @param email the email to search for
     * @return Optional containing the panel member if found
     */
    Optional<PanelMember> findByEmail(String email);
}