package org.dasher.speed.taskmanagement.domain;

import java.time.LocalDateTime;

import org.dasher.speed.taskmanagement.notificationApi.Dtos.enums.NotificationStatusEnum;

public class NotificationMessage {

    private long Id;
    private long senderId;
    private long receiverId; 
    private long appointmentId; 
    private String title; 
    private String message;
    private boolean read;

    private NotificationStatusEnum notificationStatusEnum;

    private LocalDateTime createdAt = LocalDateTime.now();
    
    // Construtores
    public NotificationMessage() {}
    
    public NotificationMessage(long Id, long senderId, long receiverId, long appointmentId, String title, String message, boolean read, NotificationStatusEnum notificationStatusEnum, LocalDateTime createdAt) {
        this.Id = Id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.appointmentId = appointmentId;
        this.title = title;
        this.message = message;
        this.read = read;
        this.notificationStatusEnum = notificationStatusEnum;
        this.createdAt = createdAt;
    }
    
    // Getters e Setters
    public long getId() {
        return Id;
    }
    
    public void setId(long Id) {
        this.Id = Id;
    }
    
    public long getSenderId() {
        return senderId;
    }
    
    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }
    
    public long getReceiverId() {
        return receiverId;
    }
    
    public void setReceiverId(long receiverId) {
        this.receiverId = receiverId;
    }
    
    public long getAppointmentId() {
        return appointmentId;
    }
    
    public void setAppointmentId(long appointmentId) {
        this.appointmentId = appointmentId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public boolean isRead() {
        return read;
    }
    
    public void setRead(boolean read) {
        this.read = read;
    }
    
    public NotificationStatusEnum getNotificationStatusEnum() {
        return notificationStatusEnum;
    }
    
    public void setNotificationStatusEnum(NotificationStatusEnum notificationStatusEnum) {
        this.notificationStatusEnum = notificationStatusEnum;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

