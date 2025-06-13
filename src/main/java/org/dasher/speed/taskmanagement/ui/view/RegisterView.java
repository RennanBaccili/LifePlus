package org.dasher.speed.taskmanagement.ui.view;

import org.dasher.speed.base.ui.component.ViewToolbar;
import org.dasher.speed.taskmanagement.domain.Enums.Role;
import org.dasher.speed.taskmanagement.domain.User;
import org.dasher.speed.taskmanagement.service.AuthenticationService;
import org.dasher.speed.taskmanagement.service.UserService;
import org.dasher.speed.taskmanagement.ui.components.RegistrationForm;
import org.dasher.speed.taskmanagement.validation.UserValidator;
import org.dasher.speed.taskmanagement.validation.UserValidator.ValidationResult;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style.Display;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.UI;

@Route("register")
@PageTitle("Registro | LifePlus")
@AnonymousAllowed
public class RegisterView extends VerticalLayout {
    private final UserService userService;
    private final AuthenticationService authService;
    private final UserValidator userValidator;
    private final RegistrationForm registrationForm;

    @Autowired
    public RegisterView(UserService userService, AuthenticationService authService, UserValidator userValidator) {
        this.userService = userService;
        this.authService = authService;
        this.userValidator = userValidator;
        this.registrationForm = new RegistrationForm();
        
        configureView();
        buildLayout();
        setupEventListeners();
    }

    private void configureView() {
        setSizeFull();
        setHeight("100vh");
        setPadding(false);
        setSpacing(false);
        setAlignItems(Alignment.STRETCH);
        addClassName("register-view");
    }

    private void buildLayout() {
        VerticalLayout toolbarLayout = createToolbarLayout();
        VerticalLayout formLayout = createFormLayout();
        
        add(toolbarLayout, formLayout);
    }

    private VerticalLayout createToolbarLayout() {
        VerticalLayout toolbarLayout = new VerticalLayout();
        toolbarLayout.setWidthFull();
        toolbarLayout.setPadding(true);
        toolbarLayout.setSpacing(false);
        toolbarLayout.setAlignItems(Alignment.START);
        toolbarLayout.getStyle().setDisplay(Display.BLOCK);

        var toolbar = new ViewToolbar("Register");
        toolbarLayout.add(toolbar);
        
        return toolbarLayout;
    }

    private VerticalLayout createFormLayout() {
        VerticalLayout formLayout = new VerticalLayout();
        formLayout.setAlignItems(Alignment.CENTER);
        formLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        formLayout.setSizeFull();
        formLayout.add(registrationForm);
        return formLayout;
    }

    private void setupEventListeners() {
        registrationForm.addRegisterListener(event -> handleRegistration());
    }

    private void handleRegistration() {
        ValidationResult validationResult = userValidator.validateRegistration(
            registrationForm.getEmail(),
            registrationForm.getPassword(),
            registrationForm.getConfirmPassword()
        );

        if (!validationResult.isValid()) {
            showNotification(validationResult.getFirstError());
            return;
        }

        registerUser();
    }

    private void registerUser() {
        try {
            User newUser = createUser();
            userService.register(newUser);
            handleSuccessfulRegistration();
        } catch (Exception e) {
            showNotification("Registration failed: " + e.getMessage());
        }
    }

    private User createUser() {
        User newUser = new User();
        newUser.setEmail(registrationForm.getEmail());
        newUser.setPassword(registrationForm.getPassword());
        newUser.setRole(Role.USER);
        return newUser;
    }

    private void handleSuccessfulRegistration() {
        String token = authService.authenticate(registrationForm.getEmail(), registrationForm.getPassword());
        VaadinSession.getCurrent().setAttribute("jwt_token", token);
        showNotification("Registration successful! Please wait while we redirect you...");
        UI.getCurrent().navigate("login");
        registrationForm.clearForm();
    }

    private void showNotification(String message) {
        Notification.show(message, 3000, Notification.Position.TOP_CENTER);
    }
}