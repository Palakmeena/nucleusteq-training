package com.palak.springcoreassignment.formatter;

import org.springframework.stereotype.Component;

/**
 * LongMessageFormatter formats messages into a detailed, structured output
 * with a header, body, and footer.
 *
 * Demonstrates: @Component on a formatter bean, interface implementation.
 */
@Component
public class LongMessageFormatter implements MessageFormatter {

    @Override
    public String format(String content) {
        if (content == null || content.isBlank()) {
            return "[EMPTY MESSAGE]";
        }
        return "======================================\n"
                + "         DETAILED MESSAGE REPORT      \n"
                + "======================================\n\n"
                + content.trim()
                + "\n\n--------------------------------------\n"
                + "  System: Spring Core Assignment      \n"
                + "  Module: Dynamic Message Formatter   \n"
                + "--------------------------------------";
    }

    @Override
    public String getType() {
        return "LONG";
    }
}
