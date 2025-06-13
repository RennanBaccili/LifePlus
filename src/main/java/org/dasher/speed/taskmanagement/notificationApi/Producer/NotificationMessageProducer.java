package org.dasher.speed.taskmanagement.notificationApi.Producer;

import org.dasher.speed.taskmanagement.config.RabbitMQConfig;
import org.dasher.speed.taskmanagement.notificationApi.Dtos.NotificationMessageRecordDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationMessageProducer {

    private final RabbitTemplate rabbitTemplate;

    public NotificationMessageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendNotification(NotificationMessageRecordDto notificationMessageRecordDto) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.NOTIFICATION_QUEUE, notificationMessageRecordDto);
    }
}
