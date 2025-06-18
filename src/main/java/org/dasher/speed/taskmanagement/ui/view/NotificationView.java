package org.dasher.speed.taskmanagement.ui.view;

import org.dasher.speed.base.ui.component.ViewToolbar;
import org.dasher.speed.taskmanagement.domain.NotificationMessage;
import org.dasher.speed.taskmanagement.notificationApi.Dtos.enums.NotificationStatusEnum;
import org.dasher.speed.taskmanagement.notificationApi.Service.NotificationClientService;
import org.dasher.speed.taskmanagement.service.AppointmentService;
import org.dasher.speed.taskmanagement.ui.components.CalendarEventHandler;

import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

@Route(value = "notifications")
@PageTitle("My Notifications | LifePlus")
@AnonymousAllowed
public class NotificationView  extends VerticalLayout {
    
    private final NotificationClientService notificationClientService;
    private final TextField filterText;
    private final Grid<NotificationMessage> grid;
    private final AppointmentService appointmentService;

    public NotificationView(NotificationClientService notificationClientService,  CalendarEventHandler eventHandler, AppointmentService appointmentService) {
        this.notificationClientService = notificationClientService;
        this.appointmentService = appointmentService;
        this.filterText = new TextField();
        this.grid = new Grid<>();

        setupToolbar();
        configureGrid();
        updateList(); 

        setSizeFull();
        addClassNames(LumoUtility.BoxSizing.BORDER, LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN,
                LumoUtility.Padding.MEDIUM, LumoUtility.Gap.SMALL);
    }

    private void setupToolbar() {
        filterText.setPlaceholder("Filter by name...");
        filterText.setAriaLabel("Filter by name");
        filterText.setMinWidth("20em");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());
        add(new ViewToolbar("Nofications List", ViewToolbar.group(filterText)));
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.addClassName("notification-grid");
        grid.addColumn(NotificationMessage::getTitle).setHeader("Title");
        grid.addColumn(NotificationMessage::getMessage).setHeader("Message");
        grid.addColumn(notification -> notification.getCreatedAt().format(
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        )).setHeader("Created Date").setSortable(true);
        grid.addColumn(NotificationMessage::isRead).setHeader("Is Read");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.addItemClickListener(item -> openNotification(item.getItem()));

        add(grid);
    }

    private void updateList() {
        String searchTerm = filterText.getValue();
        var notifications = notificationClientService.getAllNotificationsByReceiverId(1L);
        notifications.sort(Comparator.comparing(NotificationMessage::getCreatedAt).reversed());
        grid.setItems(notifications);
    }
    
    private void openNotification(NotificationMessage selectedNotification) {
        try {
            selectedNotification.setRead(true);
            notificationClientService.updateNotification(selectedNotification);
            updateList();

            if(selectedNotification.getNotificationStatusEnum() == NotificationStatusEnum.ACTION_REQUIRED) {
                this.ConfirmDialogNotification(selectedNotification);
            }
            
        } catch (Exception e) {
            showErrorNotification("Erro ao abrir agenda do médico", e.getMessage());
        }
    }

    private void showErrorNotification(String title, String message) {
        String fullMessage = String.format("%s: %s", title, message);
        Notification.show(fullMessage, 3000, Notification.Position.MIDDLE);
    }

    private void ConfirmDialogNotification(NotificationMessage selectedNotification) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Você tem um agendamento pendente");
        dialog.setText(
            selectedNotification.getMessage()
            );

        dialog.setCancelable(true);
        dialog.setRejectable(true);
        dialog.setRejectText("Rejeitar");
        dialog.addRejectListener(event -> sendNotificationConfirmation(false, selectedNotification));

        dialog.setConfirmText("Aceitar");
        dialog.addConfirmListener(event -> sendNotificationConfirmation(true, selectedNotification));

        dialog.open();
    }

    private void sendNotificationConfirmation(boolean isAccepted, NotificationMessage selectedNotification) {
        try{
            var updatedAppointment =appointmentService.acceptSchedule(isAccepted, selectedNotification);
            if(updatedAppointment == null){
                showErrorNotification("Erro ao aceitar agendamento", "Agendamento não encontrado");
                return;
            }
            selectedNotification.setNotificationStatusEnum(NotificationStatusEnum.INFO);
            notificationClientService.updateNotification(selectedNotification);
            updateList();
        }catch (Exception e) {
            showErrorNotification("Erro ao aceitar agendamento", e.getMessage());
        }
    }
}
