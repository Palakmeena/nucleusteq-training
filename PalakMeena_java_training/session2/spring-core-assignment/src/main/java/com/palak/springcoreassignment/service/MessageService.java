package com.palak.springcoreassignment.service;

import com.palak.springcoreassignment.formatter.MessageFormatter;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * MessageService resolves the correct MessageFormatter at runtime
 * based on the type parameter passed in from the controller.
 *
 * Demonstrates:
 * - Spring injecting a List of all MessageFormatter beans automatically
 * - Converting them to a Map for O(1) runtime lookup
 * - Zero if-else logic — the map.get() acts as the decision mechanism
 * - Open/Closed Principle: add a new formatter by just creating a new @Component,
 *   no changes needed anywhere else in the codebase
 */
@Service
public class MessageService {

    private final Map<String, MessageFormatter> formatterRegistry;

    public MessageService(List<MessageFormatter> formatters) {
        this.formatterRegistry = formatters.stream()
                .collect(Collectors.toMap(
                        f -> f.getType().toUpperCase(),
                        Function.identity()
                ));
    }

    public Map<String, String> getFormattedMessage(String type) {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Message type must not be empty. Supported: " + formatterRegistry.keySet());
        }

        String resolvedType = type.toUpperCase();
        MessageFormatter formatter = formatterRegistry.get(resolvedType);

        if (formatter == null) {
            throw new IllegalArgumentException(
                    "Unsupported message type: '" + type + "'. Supported types: " + formatterRegistry.keySet()
            );
        }

        String rawContent = "Welcome to NucleusTeq Java Training - Session 2. " +
                "This response is generated dynamically by the Spring-managed formatter " +
                "selected at runtime based on your request type.";

        String formatted = formatter.format(rawContent);

        Map<String, String> response = new LinkedHashMap<>();
        response.put("requestedType", resolvedType);
        response.put("formattedMessage", formatted);
        return response;
    }
}