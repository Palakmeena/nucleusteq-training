package com.nucleusteq.interviewtracker.entity;

import com.nucleusteq.interviewtracker.enums.UserRole;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PanelMember entity.
 * Tests panel member creation, default activation status,
 * onboarding flow and getters and setters work correctly.
 */
class PanelMemberTest {

    /**
     * Helper method to create a sample PanelMember
     * so we don't repeat this code in every test.
     */
    private PanelMember createSamplePanelMember() {
        return new PanelMember(
            "9876543210",
            "TechCorp",
            "Senior Developer"
        );
    }

    /**
     * Helper method to create a sample User.
     */
    private User createSampleUser() {
        return new User(
            "Rahul Verma",
            "rahul@gmail.com",
            "password123",
            UserRole.PANEL
        );
    }

    @Test
    void shouldCreatePanelMemberWithConstructor() {
        /** Act. */
        PanelMember panelMember = createSamplePanelMember();

        /** Assert. */
        assertNull(panelMember.getFullName()); // No user linked yet
        assertNull(panelMember.getEmail()); // No user linked yet
        assertEquals("9876543210", panelMember.getMobileNumber());
        assertEquals("TechCorp", panelMember.getOrganization());
        assertEquals("Senior Developer", panelMember.getDesignation());
    }

    @Test
    void shouldHaveFalseAsDefaultActiveStatus() {
        /**
         * Panel member starts inactive until HR activates them
         * and they set their password via email link.
         */
        PanelMember panelMember = createSamplePanelMember();
        assertFalse(panelMember.isActive());
    }

    @Test
    void shouldActivatePanelMemberCorrectly() {
        /** After HR activates, isActive should become true. */
        PanelMember panelMember = createSamplePanelMember();
        User user = createSampleUser();
        panelMember.setUser(user);
        panelMember.setActive(true);
        assertTrue(panelMember.isActive());
    }

    @Test
    void shouldHaveNullUserByDefault() {
        /**
         * User account is null initially.
         * It gets linked only after panel member sets password.
         */
        PanelMember panelMember = createSamplePanelMember();
        assertNull(panelMember.getUser());
    }

    @Test
    void shouldLinkUserAfterActivation() {
        /**
         * After panel member sets password,
         * their User account gets linked.
         */
        PanelMember panelMember = createSamplePanelMember();
        User user = createSampleUser();

        panelMember.setUser(user);
        panelMember.setActive(true);

        assertNotNull(panelMember.getUser());
        assertTrue(panelMember.isActive());
        assertEquals(user, panelMember.getUser());
    }

    @Test
    void shouldSetAndGetFullName() {
        PanelMember panelMember = new PanelMember();
        User user = new User("Sneha Joshi", "sneha@test.com", "pass", UserRole.PANEL);
        panelMember.setUser(user);
        assertEquals("Sneha Joshi", panelMember.getFullName());
    }

    @Test
    void shouldSetAndGetEmail() {
        PanelMember panelMember = new PanelMember();
        User user = new User("Sneha Joshi", "sneha@gmail.com", "pass", UserRole.PANEL);
        panelMember.setUser(user);
        assertEquals("sneha@gmail.com", panelMember.getEmail());
    }

    @Test
    void shouldSetAndGetOrganization() {
        PanelMember panelMember = new PanelMember();
        panelMember.setOrganization("InfoSys");
        assertEquals("InfoSys", panelMember.getOrganization());
    }

    @Test
    void shouldSetAndGetDesignation() {
        PanelMember panelMember = new PanelMember();
        panelMember.setDesignation("Tech Lead");
        assertEquals("Tech Lead", panelMember.getDesignation());
    }

    @Test
    void shouldSetAndGetMobileNumber() {
        PanelMember panelMember = new PanelMember();
        panelMember.setMobileNumber("9999999999");
        assertEquals("9999999999", panelMember.getMobileNumber());
    }

    @Test
    void shouldHaveCreatedAtSetOnConstruction() {
        /**
         * createdAt should be set automatically
         * when panel member is created.
         */
        PanelMember panelMember = createSamplePanelMember();
        assertNotNull(panelMember.getCreatedAt());
    }
}