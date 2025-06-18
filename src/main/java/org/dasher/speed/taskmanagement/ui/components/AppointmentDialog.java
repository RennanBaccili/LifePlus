package org.dasher.speed.taskmanagement.ui.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.theme.lumo.LumoUtility;

import org.dasher.speed.taskmanagement.domain.Person;
import org.dasher.speed.taskmanagement.domain.Appointment;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.List;

public class AppointmentDialog extends Dialog {

    private final Button editButton;
    private final Button cancelAppointmentButton;
    private final Button closeButton;
    private final Button saveButton;
    private Runnable onSave;

    private final ComboBox<Person> doctorField;
    private final ComboBox<Person> patientField;
    private final TimePicker startTime;
    private final TimePicker endTime;
    private final TextField titleField;
    private LocalDateTime selectedDate;

    private final Span patientDisplayField;
    private final Span statusField;

    public AppointmentDialog(String title, List<Person> doctors, LocalDateTime initialDateTime) {
        this(title, doctors, null, initialDateTime, null);
    }

    public AppointmentDialog(String title, List<Person> doctors, List<Person> patients, LocalDateTime initialDateTime) {
        this(title, doctors, patients, initialDateTime, null);
    }

    public AppointmentDialog(String title, List<Person> doctors, List<Person> patients, LocalDateTime initialDateTime, Appointment existingAppointment) {
        this.selectedDate = initialDateTime;
        setHeaderTitle(title);
        setDraggable(true);
        setResizable(true);
        setCloseOnEsc(true);
        setCloseOnOutsideClick(false);

        // Título
        titleField = new TextField("Título");
        titleField.setValue(existingAppointment != null ? existingAppointment.getTitle() : "Nova Consulta");
        titleField.setEnabled(false);

        // ComboBox de médico
        doctorField = new ComboBox<>("Médico");
        doctorField.setItems(doctors);
        doctorField.setItemLabelGenerator(person -> person.getFirstName() + " " + person.getLastName());
        doctorField.setReadOnly(existingAppointment != null);
        if (existingAppointment != null) {
            doctorField.setValue(existingAppointment.getPersonDoctor());
        }

        // ComboBox de paciente (apenas para criação)
        patientField = new ComboBox<>("Paciente");
        if (patients != null) {
            patientField.setItems(patients);
            patientField.setItemLabelGenerator(person -> person.getFirstName() + " " + person.getLastName());
        }
        patientField.setVisible(existingAppointment == null && patients != null);
        patientField.setReadOnly(existingAppointment != null);

        // TimePickers
        startTime = new TimePicker("Horário de Início");
        endTime = new TimePicker("Horário de Fim");
        initializeTimePickers(existingAppointment, initialDateTime);

        // Informações adicionais (apenas para consultas existentes)
        patientDisplayField = new Span();
        statusField = new Span();
        setDetails(existingAppointment);
        styleInfoFields(existingAppointment);

        // Layout do formulário
        FormLayout formLayout = new FormLayout();
        formLayout.add(titleField, doctorField);
        if (patientField.isVisible()) {
            formLayout.add(patientField);
        }
        formLayout.add(startTime, endTime);
        
        VerticalLayout infoLayout = new VerticalLayout(statusField, patientDisplayField);
        infoLayout.setSpacing(false);
        infoLayout.setPadding(false);

        // Botões
        closeButton = new Button("Fechar", e -> close());
        saveButton = new Button("Salvar", e -> {
            if (onSave != null) onSave.run();
            close();
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        editButton = new Button("Editar", e -> close());
        editButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        cancelAppointmentButton = new Button("Cancelar Consulta", e -> close());
        cancelAppointmentButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        HorizontalLayout buttonLayout = new HorizontalLayout(closeButton, saveButton, editButton, cancelAppointmentButton);
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);
        buttonLayout.setWidthFull();

        // Layout final
        VerticalLayout dialogLayout = new VerticalLayout(formLayout, infoLayout, buttonLayout);
        dialogLayout.setSpacing(true);
        dialogLayout.setPadding(true);
        dialogLayout.setWidth("500px");
        add(dialogLayout);

        // Visibilidade padrão
        showSaveButton(false);
        showEditButton(false);
        showCancelButton(false);
    }

    private void initializeTimePickers(Appointment existingAppointment, LocalDateTime initialDateTime) {
        startTime.setStep(Duration.ofMinutes(30));
        endTime.setStep(Duration.ofMinutes(30));

        if (existingAppointment != null) {
            startTime.setValue(existingAppointment.getAppointmentDate().toLocalTime());
            endTime.setValue(existingAppointment.getEndDate().toLocalTime());
        } else {
            startTime.setValue(initialDateTime.toLocalTime());
            endTime.setValue(initialDateTime.plusHours(1).toLocalTime());
        }
    }

    private void setDetails(Appointment appointment) {
        if (appointment != null) {
            statusField.setText("Status: " + appointment.getStatus().getDisplayName());

            Person patient = appointment.getPersonPatient();
            if (patient != null) {
                patientDisplayField.setText("Paciente: " + patient.getFirstName() + " " + patient.getLastName());
            } else {
                patientDisplayField.setText("Paciente: Não informado");
            }
        }
    }

    private void styleInfoFields(Appointment existingAppointment) {
        if(existingAppointment != null){
            CalendarEntryMapper calendarEntryMapper= new CalendarEntryMapper();
            statusField.addClassName(LumoUtility.FontWeight.BOLD);
            statusField.getStyle().set("color", calendarEntryMapper.getColorByStatus(existingAppointment.getStatus()));
        }
        patientDisplayField.addClassNames(LumoUtility.TextColor.SECONDARY);
    }

    // Getters e Setters
    public Person getSelectedDoctor() {
        return doctorField.getValue();
    }

    public void setSelectedDoctor(Person doctor) {
        doctorField.setValue(doctor);
    }

    public Person getSelectedPatient() {
        return patientField.getValue();
    }

    public void setSelectedPatient(Person patient) {
        patientField.setValue(patient);
    }

    public String getTitle() {
        return titleField.getValue();
    }

    public LocalDateTime getStartDateTime() {
        return selectedDate.toLocalDate().atTime(startTime.getValue());
    }

    public LocalDateTime getEndDateTime() {
        return selectedDate.toLocalDate().atTime(endTime.getValue());
    }

    public ComboBox<Person> getDoctorField() {
        return doctorField;
    }

    // Visibilidade dos botões
    public void showEditButton(boolean show) {
        editButton.setVisible(show);
    }

    public void showCancelButton(boolean show) {
        cancelAppointmentButton.setVisible(show);
    }

    public void showSaveButton(boolean show) {
        saveButton.setVisible(show);
    }

    // Listeners
    public void onEdit(Runnable action) {
        editButton.addClickListener(e -> action.run());
    }

    public void onCancel(Runnable action) {
        cancelAppointmentButton.addClickListener(e -> action.run());
    }

    public void onSave(Runnable action) {
        this.onSave = action;
    }
}
