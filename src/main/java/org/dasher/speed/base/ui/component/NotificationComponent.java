package org.dasher.speed.base.ui.component;

import java.util.Arrays;

import org.dasher.speed.taskmanagement.notificationApi.Service.NotificationClientService;
import org.dasher.speed.taskmanagement.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.Style;

public class NotificationComponent {
    
    @Autowired
    private final NotificationClientService notificationClientService;

    @Autowired
    private PersonService personService;

    private final MessagesButton bellButton;

    public NotificationComponent() {
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
        //var person = personService.getCurrentPerson();
        var notifications = notificationClientService.getCountNotificationsByReceiverId(1L);
        bellButton.setUnreadMessages(notifications);
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
