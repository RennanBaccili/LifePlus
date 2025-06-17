package org.dasher.speed.base.ui.component;

import java.util.Arrays;

import org.dasher.speed.taskmanagement.domain.User;
import org.dasher.speed.taskmanagement.notificationApi.Service.NotificationClientService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.Style;

public class NotificationComponentSecure {
    
    private final NotificationClientService notificationClientService;
    private final MessagesButton bellButton;

    public NotificationComponentSecure() {
        this.notificationClientService = new NotificationClientService();
        this.bellButton = createNotificationButton();

        updateNotifications();
    }

    public MessagesButton getNotificationButton() {
        return this.bellButton;
    }
    
    private MessagesButton createNotificationButton() {
        MessagesButton bellBtn = new MessagesButton();
        
        ContextMenu menu = new ContextMenu();
        menu.setOpenOnClick(true);
        menu.setTarget(bellBtn);
        menu.addItem("My Notifications");
        
        return bellBtn;
    }
    
    public void updateNotifications() {
        try {
            // Pega o usuário diretamente do contexto de segurança
            User currentUser = getCurrentUser();
            if (currentUser != null) {
                // Aqui você pode usar o ID do User diretamente 
                // ou buscar a Person associada se necessário
                var notifications = notificationClientService.getCountNotificationsByReceiverId(currentUser.getId().longValue());
                bellButton.setUnreadMessages(notifications);
            } else {
                bellButton.setUnreadMessages(0);
            }
        } catch (Exception e) {
            bellButton.setUnreadMessages(0);
        }
    }
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() 
            && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }

    public static class MessagesButton extends Button {
        private final Element numberOfNotifications;

        public MessagesButton() {
            super(VaadinIcon.BELL_O.create());
            numberOfNotifications = new Element("span");
            numberOfNotifications.getStyle()
                    .setPosition(Style.Position.ABSOLUTE)
                    .setTransform("translate(-40%, -85%)");
            numberOfNotifications.getThemeList().addAll(Arrays.asList("badge",
                    "error", "primary", "small", "pill"));
        }

        public void setUnreadMessages(int unread) {
            numberOfNotifications.setText(String.valueOf(unread));
            if (unread > 0 && numberOfNotifications.getParent() == null) {
                getElement().appendChild(numberOfNotifications);
            } else if (numberOfNotifications.getNode().isAttached()) {
                numberOfNotifications.removeFromParent();
            }
        }
    }
} 