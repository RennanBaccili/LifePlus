package org.dasher.speed.taskmanagement.ui.components;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.ItemClickEvent;
import com.vaadin.flow.component.notification.Notification;
import org.dasher.speed.taskmanagement.domain.Appointment;
import org.dasher.speed.taskmanagement.domain.Doctor;
import org.dasher.speed.taskmanagement.domain.Person;
import org.dasher.speed.taskmanagement.domain.Enums.PersonRole;
import org.dasher.speed.taskmanagement.security.SecurityService;
import org.dasher.speed.taskmanagement.service.CalendarDataManagerService;
import org.dasher.speed.taskmanagement.service.PersonService;
import org.springframework.stereotype.Component;
import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.EntryClickedEvent;
import org.vaadin.stefan.fullcalendar.FullCalendar;
import org.vaadin.stefan.fullcalendar.TimeslotClickedEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class CalendarEventHandler {
    
    private final CalendarDataManagerService dataManager;
    private final PersonService personService;
    
    public CalendarEventHandler(CalendarDataManagerService dataManager, 
                               SecurityService securityService,
                               PersonService personService) {
        this.dataManager = dataManager;
        this.personService = personService;
    }

    public void handleTimeslotClick(FullCalendar calendar, TimeslotClickedEvent event, Runnable onSuccess) {
        handleTimeslotClick(calendar, event, onSuccess, null);
        
    }

    public void handleTimeslotClick(FullCalendar calendar, TimeslotClickedEvent event, Runnable onSuccess, Doctor preSelectedDoctor) {
        LocalDateTime clickedDateTime = event.getDateTime();
        List<Person> doctors;
        
        if (preSelectedDoctor != null) {
            doctors = List.of(preSelectedDoctor.getPerson());
        } else {
            doctors = dataManager.getDoctors();
        }
        
        AppointmentDialog dialog = new AppointmentDialog(
            "Novo Agendamento",
            doctors,
            clickedDateTime
        );
        
        if (preSelectedDoctor != null) {
            dialog.setSelectedDoctor(preSelectedDoctor.getPerson());
            dialog.getDoctorField().setReadOnly(true); 
        } 
        
        dialog.showEditButton(false);
        dialog.showCancelButton(false);
        dialog.showSaveButton(true);
        
        dialog.onSave(() -> {
            try {
                Person selectedDoctor = dialog.getSelectedDoctor();
                if (selectedDoctor == null) {
                    throw new IllegalArgumentException("Por favor, selecione um médico");
                }

                Person currentPerson = personService.getCurrentPerson();
                
                Appointment appointment = createNewAppointment(dialog, selectedDoctor, currentPerson);
                
                Appointment savedAppointment = dataManager.saveAppointment(appointment);
                dataManager.addAppointmentToCalendar(calendar, savedAppointment);
                
                Notification.show("Agendamento criado com sucesso!", 3000, Notification.Position.MIDDLE);
                onSuccess.run(); // Refresh calendar after successful creation
            } catch (Exception e) {
                Notification.show("Erro ao criar agendamento: " + e.getMessage(), 
                    3000, Notification.Position.MIDDLE);
            }
        });
        
        dialog.open();
    }

    // Novo método para gerenciar agenda de paciente (perspectiva do médico)
    public void handlePatientScheduleTimeslotClick(FullCalendar calendar, TimeslotClickedEvent event, Runnable onSuccess, Person preSelectedPatient) {
        LocalDateTime clickedDateTime = event.getDateTime();
        List<Person> doctors = dataManager.getDoctors();
        
        AppointmentDialog dialog = new AppointmentDialog(
            "Novo Agendamento para " + preSelectedPatient.getFirstName(),
            doctors,
            clickedDateTime
        );
        
        // Configure buttons for new appointment
        dialog.showEditButton(false);
        dialog.showCancelButton(false);
        dialog.showSaveButton(true);
        
        // Configure save action
        dialog.onSave(() -> {
            try {
                Person selectedDoctor = dialog.getSelectedDoctor();
                if (selectedDoctor == null) {
                    throw new IllegalArgumentException("Por favor, selecione um médico");
                }

                Appointment appointment = createPatientAppointment(dialog, selectedDoctor, preSelectedPatient);
                
                Appointment savedAppointment = dataManager.saveAppointment(appointment);
                dataManager.addAppointmentToCalendar(calendar, savedAppointment);
                
                Notification.show("Agendamento criado com sucesso!", 3000, Notification.Position.MIDDLE);
                onSuccess.run(); // Refresh calendar after successful creation
            } catch (Exception e) {
                Notification.show("Erro ao criar agendamento: " + e.getMessage(), 
                    3000, Notification.Position.MIDDLE);
            }
        });
        
        dialog.open();
    }
    
    public void handleEntryCalendarClick(FullCalendar calendar, EntryClickedEvent event, Runnable onSuccess) {
        Entry clickedEntry = event.getEntry();
        Integer appointmentId = dataManager.getAppointmentId(clickedEntry);
        
        if (appointmentId == null) {
            Notification.show("Erro ao carregar agendamento", 3000, Notification.Position.MIDDLE);
            return;
        }

        handleAppointmentClick(appointmentId, calendar, clickedEntry, onSuccess);
    }
    
    public void handleEntryGridClick(Grid<Appointment> grid, ItemClickEvent<Appointment> event, Runnable onSuccess) {
        Appointment appointment = event.getItem();
        handleAppointmentClick(appointment.getId(), null, null, onSuccess);
    }

    private void handleAppointmentClick(Integer appointmentId, FullCalendar calendar, Entry clickedEntry, Runnable onSuccess) {
        try {
            Optional<Appointment> appointmentOpt = dataManager.findAppointmentById(appointmentId);
            if (appointmentOpt.isEmpty()) {
                Notification.show("Agendamento não encontrado", 3000, Notification.Position.MIDDLE);
                return;
            }

            Appointment appointment = appointmentOpt.get();
            
            AppointmentDialog dialog = new AppointmentDialog(
                "Detalhes do Agendamento",
                dataManager.getDoctors(),
                appointment.getAppointmentDate(),
                appointment
            );
            
            if (calendar != null && clickedEntry != null) {
                // Vem do calendar - permite todas as operações
                configureExistingAppointmentDialog(calendar, dialog, appointment, clickedEntry, onSuccess);
            } else {
                // Vem da grid - apenas visualização (sem edição por enquanto)
                configureViewOnlyAppointmentDialog(dialog, appointment);
            }
            
            dialog.open();
        } catch (Exception e) {
            Notification.show("Erro ao carregar detalhes do agendamento: " + e.getMessage(), 
                3000, Notification.Position.MIDDLE);
        }
    }

    private Appointment createNewAppointment(AppointmentDialog dialog, Person person_doctor, Person currentPerson) {
        Appointment appointment = new Appointment();
        appointment.setTitle(dialog.getTitle());
        appointment.setAppointmentDate(dialog.getStartDateTime());
        appointment.setEndDate(dialog.getEndDateTime());
        appointment.setStatus(Appointment.AppointmentStatus.SCHEDULING_REQUEST);
        appointment.setPersonDoctor	(person_doctor);
        appointment.setPersonPatient(currentPerson);
        
        // Add extra information if the person scheduling is a patient
        if (currentPerson.getRole() == PersonRole.PATIENT) {
            appointment.setDescription("Paciente: " + currentPerson.getFirstName() + " " + currentPerson.getLastName());
        }
        
        return appointment;
    }
    
    private Appointment createPatientAppointment(AppointmentDialog dialog, Person doctor, Person patient) {
        Appointment appointment = new Appointment();
        appointment.setTitle(dialog.getTitle());
        appointment.setAppointmentDate(dialog.getStartDateTime());
        appointment.setEndDate(dialog.getEndDateTime());
        appointment.setStatus(Appointment.AppointmentStatus.SCHEDULED);
        appointment.setPersonDoctor(doctor);
        appointment.setPersonPatient(patient);
        appointment.setDescription("Agendado pelo médico para: " + patient.getFirstName() + " " + patient.getLastName());
        
        return appointment;
    }
    
    private void configureViewOnlyAppointmentDialog(AppointmentDialog dialog, Appointment appointment) {
        // Configurar dialog para visualização com opções limitadas
        dialog.showEditButton(false);  
        dialog.showCancelButton(true);  // Permite cancelar mesmo da grid
        dialog.showSaveButton(false);
        
        // Configure cancel action (mesma funcionalidade, mas sem calendar)
        dialog.onCancel(() -> {
            try {
                dataManager.deleteAppointment(appointment.getId());
                Notification.show("Agendamento cancelado!", 3000, Notification.Position.MIDDLE);
                // Note: no calendar update since we're not in calendar view
            } catch (Exception e) {
                Notification.show("Erro ao cancelar agendamento: " + e.getMessage(), 
                    3000, Notification.Position.MIDDLE);
            }
        });
    }
    
    private void configureExistingAppointmentDialog(FullCalendar calendar, AppointmentDialog dialog, 
                                                   Appointment appointment, Entry clickedEntry,
                                                   Runnable onSuccess) {
        dialog.showEditButton(true);  
        dialog.showCancelButton(true);  
        dialog.showSaveButton(false);  
        
        // Configure edit action
        dialog.onEdit(() -> {
            dialog.showSaveButton(true);
            dialog.showEditButton(false);
            
            dialog.onSave(() -> {
                try {
                    // Update appointment dates
                    appointment.setAppointmentDate(dialog.getStartDateTime());
                    appointment.setEndDate(dialog.getEndDateTime());
                    
                    Appointment savedAppointment = dataManager.saveAppointment(appointment);
                    dataManager.updateAppointmentInCalendar(calendar, clickedEntry, savedAppointment);
                    
                    Notification.show("Agendamento atualizado com sucesso!", 3000, Notification.Position.MIDDLE);
                    onSuccess.run(); // Refresh calendar after successful update
                } catch (Exception e) {
                    Notification.show("Erro ao atualizar agendamento: " + e.getMessage(), 
                        3000, Notification.Position.MIDDLE);
                }
            });
        });
        
        // Configure cancel action
        dialog.onCancel(() -> {
            try {
                dataManager.deleteAppointment(appointment.getId());
                dataManager.removeAppointmentFromCalendar(calendar, clickedEntry);
                Notification.show("Agendamento cancelado!", 3000, Notification.Position.MIDDLE);
                onSuccess.run(); // Refresh calendar after successful deletion
            } catch (Exception e) {
                Notification.show("Erro ao cancelar agendamento: " + e.getMessage(), 
                    3000, Notification.Position.MIDDLE);
            }
        });
    }
} 