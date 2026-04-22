package com.nucleusteq.interviewtracker.entity;

import com.nucleusteq.interviewtracker.enums.UserRole;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for User entity.
 * Tests that object creation, getters, setters
 * and default values work correctly.
 */
class UserTest {

    @Test
    void shouldCreateUserWithConstructor() {
        /** Arrange, prepare test data. */
        String fullName = "Palak Meena";
        String email = "palak@gmail.com";
        String password = "password123";
        UserRole role = UserRole.HR;

        /** Act, create the object. */
        User user = new User(fullName, email, password, role);

        /** Assert, check everything is set correctly. */
        assertEquals(fullName, user.getFullName());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
        assertEquals(role, user.getRole());
    }

    @Test
    void shouldHaveFalseAsDefaultActiveStatus() {
        /** New user should always start as inactive. */
        User user = new User("Palak", "palak@gmail.com", 
                            "pass123", UserRole.CANDIDATE);
        assertFalse(user.isActive());
    }

    @Test
    void shouldSetAndGetEmail() {
        /** Test setter and getter work correctly. */
        User user = new User();
        user.setEmail("test@gmail.com");
        assertEquals("test@gmail.com", user.getEmail());
    }

    @Test
    void shouldSetActiveStatusCorrectly() {
        User user = new User();
        user.setActive(true);
        assertTrue(user.isActive());
    }
}