package org.dasher.speed.base.ui.component;

import java.util.Arrays;

import org.dasher.speed.taskmanagement.notificationApi.Service.NotificationClientService;
import org.dasher.speed.taskmanagement.service.PersonService;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.Style;

public class NotificationComponent {
    
    private final NotificationClientService notificationClientService;
    private final PersonService personService;
    private final MessagesButton bellButton;

    public NotificationComponent() {
        // Acessa os beans Spring através do helper
        this.notificationClientService = SpringContextHelper.getBean(NotificationClientService.class);
        this.personService = SpringContextHelper.getBean(PersonService.class);
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
        menu.addItem("My Notifications", e -> UI.getCurrent().navigate("notifications"));
        
        return bellBtn;
    }
    
    public void updateNotifications() {
        try {
            var person = personService.getCurrentPerson();
            var notifications = notificationClientService.getCountNotificationsByReceiverId(person.getId().longValue());
            bellButton.setUnreadMessages(notifications);
        } catch (Exception e) {
            // Em caso de erro, define 0 notificações
            bellButton.setUnreadMessages(0);
        }
    }


    private static class MessagesButton extends Button {
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
