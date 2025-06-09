package org.dasher.speed.taskmanagement.ui.components;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.shared.Registration;

public class RegistrationForm extends VerticalLayout {
    private EmailField email;
    private PasswordField password;
    private PasswordField confirmPassword;
    private Button registerButton;

    public RegistrationForm() {
        createFormLayout();
        configureForm();
    }

    private void createFormLayout() {
        FormLayout formLayout = new FormLayout();
        formLayout.getStyle()
            .set("display", "flex")
            .set("align-items", "baseline")
            .set("flex-wrap", "wrap")
            .set("max-width", "300px")
            .set("justify-content", "center")
            .set("flex-direction", "column")
            .set("margin", "0 auto");

        email = new EmailField("Email");
        password = new PasswordField("Password");
        confirmPassword = new PasswordField("Confirm Password");
        registerButton = new Button("Register", event -> fireEvent(new RegisterEvent(this)));

        configureFields();
        
        formLayout.add(
            new H2("Create Account"),
            email,
            password,
            confirmPassword,
            registerButton
        );

        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setSizeFull();
        
        add(formLayout);
    }

    private void configureFields() {
        // Email configuration
        email.setRequired(true);
        email.setErrorMessage("Please enter a valid email address");
        email.setValueChangeMode(ValueChangeMode.EAGER);
        email.addValueChangeListener(e -> validateForm());

        // Password configuration
        password.setRequired(true);
        password.setHelperText("Must contain at least 8 characters, including uppercase, lowercase, numbers and special characters");
        password.setValueChangeMode(ValueChangeMode.EAGER);
        password.addValueChangeListener(e -> {
            validateForm();
            updateConfirmPasswordValidation();
        });

        // Confirm Password configuration
        confirmPassword.setRequired(true);
        confirmPassword.setValueChangeMode(ValueChangeMode.EAGER);
        confirmPassword.addValueChangeListener(e -> validateForm());

        registerButton.addClassName("register-button");
        registerButton.setEnabled(false);
    }

    private void validateForm() {
        boolean isValid = !email.isEmpty() 
            && !password.isEmpty() 
            && !confirmPassword.isEmpty()
            && password.getValue().equals(confirmPassword.getValue());
        
        registerButton.setEnabled(isValid);
    }

    private void updateConfirmPasswordValidation() {
        if (!confirmPassword.isEmpty() && !confirmPassword.getValue().equals(password.getValue())) {
            confirmPassword.setErrorMessage("Passwords do not match");
            confirmPassword.setInvalid(true);
        } else {
            confirmPassword.setInvalid(false);
        }
    }

    private void configureForm() {
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setSizeFull();
    }

    public String getEmail() {
        return email.getValue();
    }

    public String getPassword() {
        return password.getValue();
    }

    public String getConfirmPassword() {
        return confirmPassword.getValue();
    }

    public void clearForm() {
        email.clear();
        password.clear();
        confirmPassword.clear();    
        registerButton.setEnabled(false);
    }

    // Events
    public static class RegisterEvent extends ComponentEvent<RegistrationForm> {
        public RegisterEvent(RegistrationForm source) {
            super(source, false);
        }
    }

    public Registration addRegisterListener(ComponentEventListener<RegisterEvent> listener) {
        return addListener(RegisterEvent.class, listener);
    }
} 