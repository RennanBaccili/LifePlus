package org.dasher.speed.taskmanagement.ui.components;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import org.vaadin.stefan.fullcalendar.FullCalendar;
import org.vaadin.stefan.fullcalendar.FullCalendarBuilder;
import org.vaadin.stefan.fullcalendar.CalendarViewImpl;
import org.dasher.speed.taskmanagement.domain.Doctor;
import org.dasher.speed.taskmanagement.domain.Person;
import org.dasher.speed.taskmanagement.service.CalendarDataManagerService;

public class CalendarDialog extends Dialog {
    
    public enum CalendarMode {
        VIEW_DOCTOR_SCHEDULE,    // Para pacientes verem agenda de médicos
        MANAGE_PATIENT_SCHEDULE  // Para médicos gerenciarem agenda de pacientes
    }
    
    private final FullCalendar calendar;
    private final CalendarDataManagerService dataManager;
    private final CalendarEventHandler eventHandler;
    private final Doctor doctor;
    private final Person patient;
    private final CalendarMode mode;

    // Constructor para visualizar agenda de médico (perspectiva do paciente)
    public CalendarDialog(Doctor doctor, CalendarDataManagerService dataManager, CalendarEventHandler eventHandler) {
        this(doctor, null, dataManager, eventHandler, CalendarMode.VIEW_DOCTOR_SCHEDULE);
    }

    // Constructor para gerenciar agenda de paciente (perspectiva do médico)
    public CalendarDialog(Person patient, CalendarDataManagerService dataManager, CalendarEventHandler eventHandler) {
        this(null, patient, dataManager, eventHandler, CalendarMode.MANAGE_PATIENT_SCHEDULE);
    }

    // Constructor principal
    private CalendarDialog(Doctor doctor, Person patient, CalendarDataManagerService dataManager, 
                          CalendarEventHandler eventHandler, CalendarMode mode) {
        this.doctor = doctor;
        this.patient = patient;
        this.dataManager = dataManager;
        this.eventHandler = eventHandler;
        this.mode = mode;
        
        setupTitle();
        
        setWidth("90%");
        setHeight("90%");
        
        // Initialize calendar
        this.calendar = createAndConfigureCalendar();
        
        // Setup UI
        setupLayout();
        
        // Setup event listeners
        setupEventListeners();
        
        // Load appointments based on mode
        loadAppointments();
    }

    private void setupTitle() {
        if (mode == CalendarMode.VIEW_DOCTOR_SCHEDULE && doctor != null) {
            setHeaderTitle("Agenda do Dr. " + doctor.getPerson().getFirstName() + " " + doctor.getPerson().getLastName());
        } else if (mode == CalendarMode.MANAGE_PATIENT_SCHEDULE && patient != null) {
            setHeaderTitle("Agenda de " + patient.getFirstName() + " " + patient.getLastName());
        } else {
            setHeaderTitle("Agenda");
        }
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
        if (mode == CalendarMode.VIEW_DOCTOR_SCHEDULE) {
            // Paciente agendando com médico específico
            calendar.addTimeslotClickedListener(event -> {
                eventHandler.handleTimeslotClick(calendar, event, this::refreshCalendar, doctor);
            });
        } else {
            // Médico gerenciando agenda de paciente
            calendar.addTimeslotClickedListener(event -> {
                eventHandler.handlePatientScheduleTimeslotClick(calendar, event, this::refreshCalendar, patient);
            });
        }
        
        calendar.addEntryClickedListener(event -> 
            eventHandler.handleEntryCalendarClick(calendar, event, this::refreshCalendar));
    }
    
    private void loadAppointments() {
        if (mode == CalendarMode.VIEW_DOCTOR_SCHEDULE && doctor != null) {
            dataManager.loadDoctorAppointments(calendar, doctor);
        } else if (mode == CalendarMode.MANAGE_PATIENT_SCHEDULE && patient != null) {
            dataManager.loadPatientAppointments(calendar, patient);
        }
    }

    private void refreshCalendar() {
        calendar.getEntryProvider().asInMemory().refreshAll();
    }
} 