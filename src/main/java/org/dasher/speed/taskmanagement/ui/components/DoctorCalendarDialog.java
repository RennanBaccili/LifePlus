package org.dasher.speed.taskmanagement.ui.components;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import org.vaadin.stefan.fullcalendar.FullCalendar;
import org.vaadin.stefan.fullcalendar.FullCalendarBuilder;
import org.vaadin.stefan.fullcalendar.CalendarViewImpl;
import org.dasher.speed.taskmanagement.domain.Doctor;
import org.dasher.speed.taskmanagement.service.CalendarDataManagerService;

public class DoctorCalendarDialog extends Dialog {
    
    private final FullCalendar calendar;
    private final CalendarDataManagerService dataManager;
    private final CalendarEventHandler eventHandler;
    private final Doctor doctor;

    public DoctorCalendarDialog(Doctor doctor, CalendarDataManagerService dataManager, CalendarEventHandler eventHandler) {
        this.doctor = doctor;
        this.dataManager = dataManager;
        this.eventHandler = eventHandler;
        
        // Configure dialog
        setHeaderTitle("Agenda do Dr. " + doctor.getPerson().getFirstName() + " " + doctor.getPerson().getLastName());
        setWidth("90%");
        setHeight("90%");
        
        // Initialize calendar
        this.calendar = createAndConfigureCalendar();
        
        // Setup UI
        setupLayout();
        
        // Setup event listeners
        setupEventListeners();
        
        // Load doctor's appointments
        loadDoctorAppointments();
    }

    private FullCalendar createAndConfigureCalendar() {
        FullCalendar cal = FullCalendarBuilder.create().build();
        cal.changeView(CalendarViewImpl.DAY_GRID_MONTH);
        cal.setSizeFull();
        return cal;
    }
    
    private void setupLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.add(calendar);
        layout.setFlexGrow(1, calendar);
        
        // Add close button
        Button closeButton = new Button("Fechar", e -> close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        layout.add(closeButton);
        
        add(layout);
    }
    
    private void setupEventListeners() {
        calendar.addTimeslotClickedListener(event -> {
            eventHandler.handleTimeslotClick(calendar, event, this::refreshCalendar, doctor);
        });
        calendar.addEntryClickedListener(event -> 
            eventHandler.handleEntryClick(calendar, event, this::refreshCalendar));
    }
    
    private void loadDoctorAppointments() {
        dataManager.loadDoctorAppointments(calendar, doctor);
    }

    private void refreshCalendar() {
        calendar.getEntryProvider().asInMemory().refreshAll();
    }
} 