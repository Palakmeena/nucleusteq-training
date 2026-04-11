package com.palak.springcoreassignment.formatter;

import org.springframework.stereotype.Component;

/**
 * ShortMessageFormatter formats messages into a concise, uppercase summary.
 * Truncates content to 80 characters if it exceeds that limit.
 *
 * Demonstrates: @Component on a formatter bean, interface implementation.
 */
@Component
public class ShortMessageFormatter implements MessageFormatter {

    private static final int MAX_LENGTH = 80;

    @Override
    public String format(String content) {
        if (content == null || content.isBlank()) {
            return "[EMPTY MESSAGE]";
        }
        String trimmed = content.trim();
        if (trimmed.length() > MAX_LENGTH) {
            return trimmed.substring(0, MAX_LENGTH - 3).toUpperCase() + "...";
        }
        return trimmed.toUpperCase();
    }

    @Override
    public String getType() {
        return "SHORT";
    }
}
