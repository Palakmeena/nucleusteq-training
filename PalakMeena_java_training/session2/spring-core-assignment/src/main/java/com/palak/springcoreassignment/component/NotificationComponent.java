package com.palak.springcoreassignment.component;

import org.springframework.stereotype.Component;

/**
 * NotificationComponent is a reusable Spring-managed component
 * responsible for building notification messages.
 *
 * Demonstrates: @Component usage and separation of utility logic
 * from business logic (which stays in the Service layer).
 */
@Component
public class NotificationComponent {

    public String buildMessage(String recipient, String event) {
        return String.format(
                "Hello %s, this is an automated notification. " +
                        "The following event was triggered on your account: [%s]. " +
                        "If this was not you, please contact support immediately.",
                recipient, event
        );
    }

    public String buildSubject(String event) {
        return "Account Notification: " + event;
    }
}