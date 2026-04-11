package com.palak.springcoreassignment.controller;

import com.palak.springcoreassignment.model.ApiResponse;
import com.palak.springcoreassignment.model.NotificationRequest;
import com.palak.springcoreassignment.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * NotificationController exposes the API to trigger notifications.
 *
 * Demonstrates:
 * - Constructor injection
 * - Controller delegating entirely to the service
 * - @RequestBody for proper REST POST design (not query params)
 */
@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // POST /notifications/send
    // Body: { "recipient": "Palak", "event": "LOGIN" }
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<Map<String, String>>> sendNotification(
            @RequestBody NotificationRequest request) {

        Map<String, String> result = notificationService.sendNotification(
                request.getRecipient(),
                request.getEvent()
        );
        return ResponseEntity.ok(
                ApiResponse.ok("Notification dispatched successfully.", result)
        );
    }
}