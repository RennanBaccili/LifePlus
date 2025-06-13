package org.dasher.speed.taskmanagement.service;

import org.dasher.speed.taskmanagement.domain.Appointment;
import org.dasher.speed.taskmanagement.domain.NotificationMessage;
import org.dasher.speed.taskmanagement.notificationApi.Dtos.enums.NotificationStatusEnum;
import org.dasher.speed.taskmanagement.notificationApi.Controller.NotificationMessageController;
import org.dasher.speed.taskmanagement.notificationApi.Dtos.NotificationMessageRecordDto;
import org.springframework.stereotype.Service;

@Service
public class NotificationMessageService {

    private final NotificationMessageController notificationMessageController;

    public NotificationMessageService(NotificationMessageController notificationMessageController) {
        this.notificationMessageController = notificationMessageController;
    }

    public void sendNotificationByAppointment(Appointment appointment) {
        var notificationMessageRecordDto = setNotificationMessageByAppointment(appointment);
        notificationMessageController.sendNotification(notificationMessageRecordDto);
    }

    public NotificationMessageRecordDto setNotificationMessageByAppointment(Appointment appointment) {
        var NotificationMessage = new NotificationMessage();
        NotificationMessage.setSenderId(appointment.getPersonPatient().getId().longValue());
        NotificationMessage.setReceiverId(appointment.getPersonDoctor().getId().longValue());   
        NotificationMessage.setAppointmentId(appointment.getId().longValue());
        NotificationMessage.setTitle("Requisição de agendamento"); 
        NotificationMessage.setRead(false); 
        NotificationMessage = setMessageAndStatusByAppointment(NotificationMessage, appointment);


        return new NotificationMessageRecordDto(
            NotificationMessage.getSenderId(),
            NotificationMessage.getReceiverId(),
            NotificationMessage.getAppointmentId(),
            NotificationMessage.getTitle(),
            NotificationMessage.getMessage(),
            NotificationMessage.isRead(),
            NotificationMessage.getNotificationStatusEnum()
        );
    }

    public NotificationMessage setMessageAndStatusByAppointment(NotificationMessage notificationMessage, Appointment appointment) {
        String message = "";

        if (appointment.getStatus() == Appointment.AppointmentStatus.SCHEDULING_REQUEST) {
            notificationMessage.setNotificationStatusEnum(NotificationStatusEnum.ACTION_REQUIRED);
            message = "O usuario " + appointment.getPersonPatient().getFirstName() + " solicitou um agendamento com o medico " + appointment.getPersonPatient().getFirstName() + " para o dia " + appointment.getAppointmentDate();
        }

        notificationMessage.setMessage(message);
        return notificationMessage;
    }
}
