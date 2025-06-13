package org.dasher.speed.taskmanagement.notificationApi.Dtos;

import java.io.Serializable;
import org.dasher.speed.taskmanagement.notificationApi.Dtos.enums.NotificationStatusEnum;

public record NotificationMessageRecordDto(
    Long senderId,
    Long receiverId,
    Long appointmentId,
    String title,
    String message,
    boolean read,
    NotificationStatusEnum notificationStatusEnum
) implements Serializable {
}