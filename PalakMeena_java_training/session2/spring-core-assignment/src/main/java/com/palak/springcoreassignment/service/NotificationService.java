package com.palak.springcoreassignment.service;

import com.palak.springcoreassignment.component.NotificationComponent;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * NotificationService contains business logic for sending notifications.
 * It delegates message construction to NotificationComponent,
 * keeping utility logic separate from business logic.
 *
 * Demonstrates:
 * - @Service with constructor injection
 * - @Component injected into @Service via constructor
 * - Separation of concerns between service and component
 */
@Service
public class NotificationService {

    private final NotificationComponent notificationComponent;

    public NotificationService(NotificationComponent notificationComponent) {
        this.notificationComponent = notificationComponent;
    }

    public Map<String, String> sendNotification(String recipient, String event) {
        validateInputs(recipient, event);

        String subject = notificationComponent.buildSubject(event);
        String message = notificationComponent.buildMessage(recipient, event);

        Map<String, String> response = new LinkedHashMap<>();
        response.put("status", "Notification sent successfully");
        response.put("recipient", recipient);
        response.put("event", event);
        response.put("subject", subject);
        response.put("message", message);
        return response;
    }

    private void validateInputs(String recipient, String event) {
        if (recipient == null || recipient.isBlank()) {
            throw new IllegalArgumentException("Recipient name must not be empty.");
        }
        if (event == null || event.isBlank()) {
            throw new IllegalArgumentException("Event type must not be empty.");
        }
    }
}