package com.sidoso.profissional.model;

public class Message {
    private int id;
    private int senderId;
    private int receptorId;
    private String receptorEmail;
    private String message;
    private String senderAt;

    public Message(int senderId, int receptorId, String receptorEmail, String message, String senderAt) {
        this.senderId = senderId;
        this.receptorId = receptorId;
        this.receptorEmail = receptorEmail;
        this.message = message;
        this.senderAt = senderAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceptorId() {
        return receptorId;
    }

    public void setReceptorId(int receptorId) {
        this.receptorId = receptorId;
    }

    public String getReceptorEmail() {
        return receptorEmail;
    }

    public void setReceptorEmail(String receptorEmail) {
        this.receptorEmail = receptorEmail;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderAt() {
        return senderAt;
    }

    public void setSenderAt(String senderAt) {
        this.senderAt = senderAt;
    }
}
