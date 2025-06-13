package org.dasher.speed.taskmanagement.notificationApi.Controller;

import org.dasher.speed.taskmanagement.notificationApi.Dtos.NotificationMessageRecordDto;
import org.dasher.speed.taskmanagement.notificationApi.Producer.NotificationMessageProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/vaadin/notifications")
public class NotificationMessageController {

    private final NotificationMessageProducer producer;

    public NotificationMessageController(NotificationMessageProducer producer) {
        this.producer = producer;
    }

    @PostMapping
    public ResponseEntity<String> sendNotification(@RequestBody NotificationMessageRecordDto dto) {
        producer.sendNotification(dto);
        return ResponseEntity.accepted().body("Notificação enviada para a fila com sucesso.");
    }
}
