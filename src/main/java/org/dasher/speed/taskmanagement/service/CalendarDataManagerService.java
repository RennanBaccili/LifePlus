package org.dasher.speed.taskmanagement.service;

import com.vaadin.flow.component.notification.Notification;
import org.dasher.speed.taskmanagement.domain.Appointment;
import org.dasher.speed.taskmanagement.domain.Person;
import org.dasher.speed.taskmanagement.domain.Doctor;
import org.dasher.speed.taskmanagement.ui.components.CalendarEntryMapper;
import org.springframework.stereotype.Service;
import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.FullCalendar;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CalendarDataManagerService {
    
    private final AppointmentService appointmentService;
    private final PersonService personService;
    private final CalendarEntryMapper entryMapper;
    private final NotificationMessageService notificationMessageService;
    
    public CalendarDataManagerService(AppointmentService appointmentService, 
                              PersonService personService,
                              CalendarEntryMapper entryMapper,
                              NotificationMessageService notificationMessageService) {
        this.appointmentService = appointmentService;
        this.personService = personService;
        this.entryMapper = entryMapper;
        this.notificationMessageService = notificationMessageService;
    }
    
    public void loadExistingAppointments(FullCalendar calendar) {
        try {
            List<Appointment> appointments = appointmentService.findByPerson(personService.getCurrentPerson());
            
            for (Appointment appointment : appointments) {
                addAppointmentToCalendar(calendar, appointment);
            }
        } catch (Exception e) {
            Notification.show("Erro ao carregar agendamentos: " + e.getMessage(), 
                3000, Notification.Position.MIDDLE);
        }
    }
    
    public void loadDoctorAppointments(FullCalendar calendar, Doctor doctor) {
        try {
            List<Appointment> appointments = appointmentService.findByDoctor(doctor);
            
            for (Appointment appointment : appointments) {
                addAppointmentToCalendar(calendar, appointment);
            }
        } catch (Exception e) {
            Notification.show("Erro ao carregar agendamentos do médico: " + e.getMessage(), 
                3000, Notification.Position.MIDDLE);
        }
    }
    
    public void loadPatientAppointments(FullCalendar calendar, Person patient) {
        try {
            List<Appointment> appointments = appointmentService.findByPerson(patient);
            
            for (Appointment appointment : appointments) {
                addAppointmentToCalendar(calendar, appointment);
            }
        } catch (Exception e) {
            Notification.show("Erro ao carregar agendamentos do paciente: " + e.getMessage(), 
                3000, Notification.Position.MIDDLE);
        }
    }
    
    public void addAppointmentToCalendar(FullCalendar calendar, Appointment appointment) {
        Entry entry = entryMapper.createCalendarEntry(appointment);
        calendar.getEntryProvider().asInMemory().addEntry(entry);
    }
    
    public void updateAppointmentInCalendar(FullCalendar calendar, Entry oldEntry, Appointment appointment) {
        calendar.getEntryProvider().asInMemory().removeEntry(oldEntry);
        entryMapper.removeEntry(oldEntry);        
        addAppointmentToCalendar(calendar, appointment);
    }
    
    public void removeAppointmentFromCalendar(FullCalendar calendar, Entry entry) {
        calendar.getEntryProvider().asInMemory().removeEntry(entry);
        entryMapper.removeEntry(entry);
    }
    
    public List<Person> getDoctors() {
        return personService.findAllDoctors().stream()
            .collect(Collectors.toList());
    }
    
    public Appointment saveAppointment(Appointment appointment) {
        appointmentService.validateAppointment(appointment);
        var appointmentSaved = appointmentService.save(appointment);
        notificationMessageService.sendNotificationByAppointment(appointmentSaved);
        return appointmentSaved;
    }
    
    public Optional<Appointment> findAppointmentById(Integer id) {
        return appointmentService.getAppointmentById(id);
    }
    
    public void deleteAppointment(Integer id) {
        appointmentService.delete(id);
    }
    
    public Integer getAppointmentId(Entry entry) {
        return entryMapper.getAppointmentId(entry);
    }
} 