package org.dasher.speed.taskmanagement.ui.view;

import org.dasher.speed.base.ui.component.ViewToolbar;
import org.dasher.speed.taskmanagement.domain.Enums.Role;
import org.dasher.speed.taskmanagement.domain.User;
import org.dasher.speed.taskmanagement.service.AuthenticationService;
import org.dasher.speed.taskmanagement.service.UserService;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.dom.Style.Display;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.annotation.Autowired;

@Route("register")
@PageTitle("Registro | LifePlus")
@AnonymousAllowed
public class RegisterView extends VerticalLayout {



    private final UserService userService;
    private final AuthenticationService authService;
    private EmailField email;
    private PasswordField password;
    private PasswordField confirmPassword;

    @Autowired
    public RegisterView(UserService userService, AuthenticationService authService) {
        this.userService = userService;
        this.authService = authService;
        
        // Layout principal da página
        setSizeFull();
        setHeight("100vh");
        setPadding(false);
        setSpacing(false);
        setAlignItems(Alignment.STRETCH);
        addClassName("register-view");

        // Toolbar Layout
        VerticalLayout toolbarLayout = new VerticalLayout();
        toolbarLayout.setWidthFull();
        toolbarLayout.setPadding(true);
        toolbarLayout.setSpacing(false);
        toolbarLayout.setAlignItems(Alignment.START);
        toolbarLayout.getStyle().setDisplay(Display.BLOCK);

        var toolbar = new ViewToolbar("Register");
        toolbarLayout.add(toolbar);

        // Registration Form Layout
        VerticalLayout formLayout = new VerticalLayout();
        formLayout.setAlignItems(Alignment.CENTER);
        formLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        formLayout.setSizeFull();

        // Create registration form
        VerticalLayout registrationForm = createRegistrationForm();
        formLayout.add(new H2("Create Account"), registrationForm);

        // Add layouts to main layout
        add(toolbarLayout, formLayout);
    }

    private VerticalLayout createRegistrationForm() {
                  FormLayout formLayout = new FormLayout();
          formLayout.getStyle()
              .set("display", "flex")
              .set("align-items", "baseline")
              .set("flex-wrap", "wrap")
              .set("max-width", "300px")
              .set("justify-content", "center")
              .set("flex-direction", "column")
              .set("margin", "0 auto");

          
        VerticalLayout container = new VerticalLayout();
        container.setAlignItems(Alignment.CENTER);
        container.setJustifyContentMode(JustifyContentMode.CENTER);
        container.setSizeFull();

        email = new EmailField("Email");
        email.setRequired(true);
        email.setErrorMessage("Please enter a valid email address");

        password = new PasswordField("Password");
        password.setRequired(true);
        password.setErrorMessage("Please enter a password");

        confirmPassword = new PasswordField("Confirm Password");
        confirmPassword.setRequired(true);
        confirmPassword.setErrorMessage("Please confirm your password");

        Button registerButton = new Button("Register", event -> register());
        registerButton.addClassName("register-button");

        formLayout.add(email, password, confirmPassword, registerButton);
         container.add(formLayout);
         return container;
    }

    private void register() {
        if (!password.getValue().equals(confirmPassword.getValue())) {
            Notification.show("Passwords do not match", 3000, Notification.Position.TOP_CENTER);
            return;
        }

        if (userService.emailExists(email.getValue())) {
            Notification.show("Email already registered", 3000, Notification.Position.TOP_CENTER);
            return;
        }

        try {
            User newUser = new User();
            newUser.setEmail(email.getValue());
            newUser.setPassword(password.getValue());
            newUser.setRole(Role.USER);

            User registeredUser = userService.register(newUser);
            String token = authService.authenticate(email.getValue(), password.getValue());
            VaadinSession.getCurrent().setAttribute("jwt_token", token);
            Notification.show("Registration successful! Please wait while we redirect you...", 
                3000, Notification.Position.TOP_CENTER);
            
            } catch (Exception e) {
            Notification.show("Registration failed: " + e.getMessage(), 
                3000, Notification.Position.TOP_CENTER);
        }
    }
}