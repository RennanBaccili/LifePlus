package org.dasher.speed.taskmanagement.ui.view;

import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;

import org.dasher.speed.taskmanagement.domain.Doctor;
import org.dasher.speed.taskmanagement.domain.Enums.Role;
import org.dasher.speed.taskmanagement.domain.Patient;
import org.dasher.speed.taskmanagement.domain.Person;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

@Route("person-registration")
@PageTitle("Person Input")
@Menu(order = 0, icon = "vaadin:user", title = "Person Input")
@PermitAll 
public class PersonInputView extends VerticalLayout {

    private final Person person = new Person();
    private Doctor doctor;
    private Patient patient;

    // Person fields
    private final TextField firstName = new TextField("First Name");
    private final TextField lastName = new TextField("Last Name");
    private final TextField phone = new TextField("Phone");
    private final TextField cpf = new TextField("CPF");
    private final ComboBox<String> gender = new ComboBox<>("Gender");
    private final ComboBox<String> maritalStatus = new ComboBox<>("Marital Status");
    private final ComboBox<Role> role = new ComboBox<>("Role");

    // Doctor specific fields
    private final FormLayout doctorFields = new FormLayout();
    private final TextField licenseNumber = new TextField("License Number");
    private final TextField licenseState = new TextField("License State");
    private final TextField medicalSpecialty = new TextField("Medical Specialty");
    private final TextField digitalSignature = new TextField("Digital Signature");

    // Patient specific fields
    private final FormLayout patientFields = new FormLayout();
    private final TextField healthPlan = new TextField("Health Plan");

    public PersonInputView() {
        addClassName("person-input-view");
        setSizeFull();
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        addClassNames(LumoUtility.Padding.MEDIUM);

        add(createTitle());
        add(createFormLayout());
        add(createButtonLayout());

        setupFields();
    }

    private Component createTitle() {
        return new H2("Person Registration");
    }

    private Component createFormLayout() {
        FormLayout formLayout = new FormLayout();
        
        // Configure gender options
        gender.setItems("Male", "Female", "Other");
        
        // Configure marital status options
        maritalStatus.setItems("Single", "Married", "Divorced", "Widowed");
        
        // Configure role selection
        role.setItems(Role.values());
        role.addValueChangeListener(event -> {
            if (event.getValue() == Role.DOCTOR) {
                doctorFields.setVisible(true);
                patientFields.setVisible(false);
            } else if (event.getValue() == Role.PATIENT) {
                doctorFields.setVisible(false);
                patientFields.setVisible(true);
            } else {
                doctorFields.setVisible(false);
                patientFields.setVisible(false);
            }
        });

        // Add person fields
        formLayout.add(
            firstName, lastName,
            phone, cpf,
            gender, maritalStatus,
            role
        );

        // Setup doctor fields
        doctorFields.add(
            licenseNumber, licenseState,
            medicalSpecialty, digitalSignature
        );
        doctorFields.setVisible(false);

        // Setup patient fields
        patientFields.add(healthPlan);
        patientFields.setVisible(false);

        // Add role-specific fields
        formLayout.add(doctorFields, patientFields);
        formLayout.setHeight("100%");
        formLayout.addClassNames(
            LumoUtility.Padding.MEDIUM,
            LumoUtility.BoxSizing.BORDER,
            LumoUtility.Background.CONTRAST_5
        );
        formLayout.getStyle().set("max-width", "80%");
        formLayout.getStyle().set("width", "80%");
        formLayout.getStyle().set("margin", "0 auto");

        return formLayout;
    }

    private Component createButtonLayout() {
        Button save = new Button("Save");
        Button cancel = new Button("Cancel");

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickListener(event -> {
            if (validateAndSave()) {
                Notification.show("Registration successful!", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                clearForm();
            }
        });

        cancel.addClickListener(event -> clearForm());

        // Create a horizontal layout for buttons
        HorizontalLayout buttonLayout = new HorizontalLayout(save, cancel);
        buttonLayout.setSpacing(true);
        buttonLayout.setPadding(false);

        // Set fixed width for buttons
        save.getStyle().set("width", "200px");
        cancel.getStyle().set("width", "200px");

        
        buttonLayout.getStyle().set("max-width", "80%");
        buttonLayout.getStyle().set("width", "100%");

        buttonLayout.getStyle().set("justify-content", "end");
        buttonLayout.getStyle().set("align-items", "end");
        buttonLayout.getStyle().set("margin", "0 auto");

        return buttonLayout;
    }

    private void setupFields() {
        // Add validators and configure fields as needed
        firstName.setRequired(true);
        lastName.setRequired(true);
        cpf.setRequired(true);
        role.setRequired(true);
    }

    private boolean validateAndSave() {
        try {
            // Basic validation
            if (firstName.isEmpty() || lastName.isEmpty() || cpf.isEmpty() || role.isEmpty()) {
                throw new ValidationException("Please fill in all required fields");
            }

            // Set person data
            person.setFirstName(firstName.getValue());
            person.setLastName(lastName.getValue());
            person.setPhone(phone.getValue());
            person.setCpf(cpf.getValue());
            person.setGender(gender.getValue());
            person.setMaritalStatus(maritalStatus.getValue());

            // Handle role-specific data
            if (role.getValue() == Role.DOCTOR) {
                doctor = new Doctor();
                doctor.setLicenseNumber(licenseNumber.getValue());
                doctor.setLicenseState(licenseState.getValue());
                doctor.setMedicalSpecialty(medicalSpecialty.getValue());
                doctor.setDigitalSignature(digitalSignature.getValue());
                // TODO: Save doctor data
            } else if (role.getValue() == Role.PATIENT) {
                patient = new Patient();
                patient.setHealthPlan(healthPlan.getValue());
                // TODO: Save patient data
            }

            // TODO: Save person data and establish relationships

            return true;
        } catch (ValidationException e) {
            Notification.show(e.getMessage(), 3000, Notification.Position.TOP_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return false;
        }
    }

    private void clearForm() {
        // Clear person fields
        firstName.clear();
        lastName.clear();
        phone.clear();
        cpf.clear();
        gender.clear();
        maritalStatus.clear();
        role.clear();

        // Clear doctor fields
        licenseNumber.clear();
        licenseState.clear();
        medicalSpecialty.clear();
        digitalSignature.clear();

        // Clear patient fields
        healthPlan.clear();

        // Hide role-specific fields
        doctorFields.setVisible(false);
        patientFields.setVisible(false);
    }

    private static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }
}
