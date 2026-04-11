package com.palak.springcoreassignment.controller;

import com.palak.springcoreassignment.model.ApiResponse;
import com.palak.springcoreassignment.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * MessageController exposes the Dynamic Message Formatter API.
 */
@RestController
@RequestMapping("/message")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    // GET /message?type=SHORT
    // GET /message?type=LONG
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, String>>> getMessage(
            @RequestParam(defaultValue = "SHORT") String type) {

        Map<String, String> result = messageService.getFormattedMessage(type);
        return ResponseEntity.ok(
                ApiResponse.ok("Message formatted successfully.", result));
    }
}