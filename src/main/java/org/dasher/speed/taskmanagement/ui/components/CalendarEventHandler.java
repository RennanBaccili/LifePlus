package org.dasher.speed.taskmanagement.ui.components;

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
        
        // If we have a pre-selected doctor, set it
        if (preSelectedDoctor != null) {
            dialog.setSelectedDoctor(preSelectedDoctor.getPerson());
            dialog.getDoctorField().setReadOnly(true); // Prevent changing the doctor
        }
        
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
    
    public void handleEntryClick(FullCalendar calendar, EntryClickedEvent event, Runnable onSuccess) {
        Entry clickedEntry = event.getEntry();
        Integer appointmentId = dataManager.getAppointmentId(clickedEntry);
        
        if (appointmentId == null) {
            Notification.show("Erro ao carregar agendamento", 3000, Notification.Position.MIDDLE);
            return;
        }

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
            
            configureExistingAppointmentDialog(calendar, dialog, appointment, clickedEntry, onSuccess);
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