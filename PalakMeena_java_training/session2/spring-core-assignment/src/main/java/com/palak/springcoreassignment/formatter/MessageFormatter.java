package com.palak.springcoreassignment.formatter;

/**
 * MessageFormatter defines the contract for all message formatters.
 *
 * Each implementation is a Spring @Component and registers itself
 * by returning a unique type string via getType().
 * The MessageService uses this to resolve the correct formatter at runtime
 * without any if-else logic.
 */
public interface MessageFormatter {

    String format(String content);

    String getType();
}