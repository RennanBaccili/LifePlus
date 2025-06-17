package org.dasher.speed.taskmanagement.ui.view;

import org.dasher.speed.base.ui.component.ViewToolbar;
import org.dasher.speed.taskmanagement.domain.NotificationMessage;
import org.dasher.speed.taskmanagement.notificationApi.Dtos.enums.NotificationStatusEnum;
import org.dasher.speed.taskmanagement.notificationApi.Service.NotificationClientService;
import org.dasher.speed.taskmanagement.service.NotificationMessageService;
import org.dasher.speed.taskmanagement.ui.components.CalendarEventHandler;

import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

@Route(value = "notifications")
@PageTitle("My Notifications | LifePlus")
@AnonymousAllowed
public class NotificationView  extends VerticalLayout {
    
    private final NotificationMessageService notificationMessageService;
    private final NotificationClientService notificationClientService;
    private final TextField filterText;
    private final Grid<NotificationMessage> grid;
    private final Span Status;

    public NotificationView(NotificationClientService notificationClientService, NotificationMessageService notificationMessageService,  CalendarEventHandler eventHandler) {
        this.notificationClientService = notificationClientService;
        this.notificationMessageService = notificationMessageService;
        this.filterText = new TextField();
        this.grid = new Grid<>();
        this.Status = new Span();

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
        add(new ViewToolbar("Doctors List", ViewToolbar.group(filterText)));
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.addClassName("notification-grid");
        grid.addColumn(NotificationMessage::getTitle).setHeader("Title");
        grid.addColumn(NotificationMessage::getMessage).setHeader("Message");
        grid.addColumn(NotificationMessage::getCreatedAt).setHeader("Created At");
        grid.addColumn(NotificationMessage::isRead).setHeader("Is Read");

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        
        grid.addItemClickListener(item -> openNotification(item.getItem()));
        
        add(grid);
    }

    private void updateList() {
        String searchTerm = filterText.getValue();
        grid.setItems(notificationClientService.getAllNotificationsByReceiverId(1L));
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
        Status.setVisible(false);

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
        //TODO: Implementar a lógica de envio da notificação de confirmação
        
        notificationMessageService.sendNotificationConfirmation(isAccepted, selectedNotification.getAppointmentId());
        updateList();
    }
}
