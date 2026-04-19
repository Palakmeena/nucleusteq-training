package com.palak.springadvanceassignment.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * NotificationServiceClient simulates calling an external notification service.
 * Demonstrates:
 * - Separation of concerns (notification logic is NOT inside TodoService)
 * - @Service so Spring manages it as a bean
 * - Constructor injection when used in TodoService
 * - SLF4J logging
 */
@Service
public class NotificationServiceClient {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceClient.class);

    public void sendTodoCreatedNotification(Long todoId, String title) {
        log.info("[NotificationServiceClient] Notification sent for new TODO → id: {}, title: '{}'", todoId, title);
    }

    public void sendTodoDeletedNotification(Long todoId) {
        log.info("[NotificationServiceClient] Notification sent for deleted TODO → id: {}", todoId);
    }
}