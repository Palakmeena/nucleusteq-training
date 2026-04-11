package com.palak.springcoreassignment.model;

public class NotificationRequest {

    private String recipient;
    private String event;

    public NotificationRequest() {}

    public NotificationRequest(String recipient, String event) {
        this.recipient = recipient;
        this.event = event;
    }

    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }

    public String getEvent() { return event; }
    public void setEvent(String event) { this.event = event; }
}