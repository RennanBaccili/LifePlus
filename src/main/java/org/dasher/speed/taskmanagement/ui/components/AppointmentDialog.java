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
    private final TimePicker startTime;
    private final TimePicker endTime;
    private final TextField titleField;
    private LocalDateTime selectedDate;

    public AppointmentDialog(String title, List<Person> doctors, LocalDateTime initialDateTime) {
        this(title, doctors, initialDateTime, null);
    }

    public AppointmentDialog(String title, List<Person> doctors, LocalDateTime initialDateTime, Appointment existingAppointment) {
        setHeaderTitle(title);
        this.selectedDate = initialDateTime;
        
        // Campos do formulário
        FormLayout formLayout = new FormLayout();
        
        // Campo de título
        titleField = new TextField("Título");
        titleField.setValue(existingAppointment != null ? existingAppointment.getTitle() : "Nova Consulta");
        titleField.setEnabled(false);
        // Seleção de médico
        doctorField = new ComboBox<>("Médico");
        doctorField.setItems(doctors);
        doctorField.setItemLabelGenerator(person -> 
            person.getFirstName() + " " + person.getLastName());
        
        if (existingAppointment != null) {
            Person doctorPerson = existingAppointment.getPersonDoctor();
            doctorField.setValue(doctorPerson);
            doctorField.setReadOnly(true);
        }
        
        startTime = new TimePicker("Horário de Início");
        startTime.setValue(initialDateTime.toLocalTime());
        startTime.setStep(Duration.ofMinutes(30));
        
        endTime = new TimePicker("Horário de Fim");
        endTime.setValue(initialDateTime.plusHours(1).toLocalTime());
        endTime.setStep(Duration.ofMinutes(30));
        
        // Se for um agendamento existente, mostra os horários atuais
        if (existingAppointment != null) {
            startTime.setValue(existingAppointment.getAppointmentDate().toLocalTime());
            endTime.setValue(existingAppointment.getEndDate().toLocalTime());
        }
        
        // Adiciona os campos ao form
        formLayout.add(titleField, doctorField, startTime, endTime);
        
        // Layout principal
        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(true);
        dialogLayout.add(formLayout);
        
        // Buttons
        closeButton = new Button("Fechar", e -> close());
        
        saveButton = new Button("Salvar", e -> {
            if (onSave != null) {
                onSave.run();
            }
            close();
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
        editButton = new Button("Editar", e -> {
            close();
        });
        editButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
        cancelAppointmentButton = new Button("Cancelar Consulta", e -> {
            close();
        });
        cancelAppointmentButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        
        // Button layout
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);
        
        buttonLayout.add(closeButton);
        buttonLayout.add(saveButton);
        buttonLayout.add(editButton);
        buttonLayout.add(cancelAppointmentButton);
        
        dialogLayout.add(buttonLayout);
        add(dialogLayout);
        
        // Dialog configuration
        setCloseOnEsc(true);
        setCloseOnOutsideClick(false);
        setDraggable(true);
        setResizable(true);
        
        // Por padrão, esconde os botões de ação
        showSaveButton(false);
        showEditButton(false);
        showCancelButton(false);
    }

    // Getters para os valores dos campos
    public Person getSelectedDoctor() {
        return doctorField.getValue();
    }
    
    public ComboBox<Person> getDoctorField() {
        return doctorField;
    }
    
    public void setSelectedDoctor(Person doctor) {
        doctorField.setValue(doctor);
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

    // Métodos para controlar a visibilidade dos botões
    public void showEditButton(boolean show) {
        editButton.setVisible(show);
    }

    public void showCancelButton(boolean show) {
        cancelAppointmentButton.setVisible(show);
    }
    
    public void showSaveButton(boolean show) {
        saveButton.setVisible(show);
    }

    // Métodos para adicionar listeners aos botões
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
