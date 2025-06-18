package org.dasher.speed.taskmanagement.ui.components;

import org.dasher.speed.taskmanagement.domain.Appointment;
import org.vaadin.stefan.fullcalendar.Entry;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Responsible for mapping between Appointment entities and FullCalendar Entry objects
 */
@Component
public class CalendarEntryMapper {
    
    private final Map<Entry, Integer> entryToAppointmentId = new HashMap<>();
    
    public Entry createCalendarEntry(Appointment appointment) {
        Entry entry = new Entry();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String startHour = appointment.getAppointmentDate().format(formatter);
        String endHour = appointment.getEndDate().format(formatter);
        // Basic information
        entry.setTitle("Id: " + appointment.getId().toString() + " - " + startHour + " - " + endHour);
        entry.setStart(appointment.getAppointmentDate());
        entry.setEnd(appointment.getEndDate());
        entry.setColor(getColorByStatus(appointment.getStatus()));
        
        // Add detailed information in description
        StringBuilder description = new StringBuilder();
        description.append("Médico: ").append(appointment.getPersonDoctor().getFirstName())
                  .append(" ").append(appointment.getPersonDoctor().getLastName());
        
        if (appointment.getPersonPatient() != null) {
            description.append("\nAgendado por: ").append(appointment.getPersonPatient().getFirstName())
                      .append(" ").append(appointment.getPersonPatient().getLastName());
        }
        
        if (appointment.getDescription() != null) {
            description.append("\n").append(appointment.getDescription());
        }
        
        entry.setDescription(description.toString());
        
        // Store the appointment ID for reference
        entryToAppointmentId.put(entry, appointment.getId());
        
        return entry;
    }
    
    public Integer getAppointmentId(Entry entry) {
        return entryToAppointmentId.get(entry);
    }
    
    public void removeEntry(Entry entry) {
        entryToAppointmentId.remove(entry);
    }
    
    public void clearMappings() {
        entryToAppointmentId.clear();
    }
    
    public String getColorByStatus(Appointment.AppointmentStatus status) {
        switch (status) {
            case SCHEDULING_REQUEST: return "#FF9800"; // Orange
            case SCHEDULED: return "#2196F3"; // Blue
            case IN_PROGRESS: return "#FF9800"; // Orange
            case COMPLETED: return "#9E9E9E"; // Gray
            case CANCELLED: return "#F44336"; // Red
            case NO_SHOW: return "#795548"; // Brown
            default: return "#2196F3";
        }
    }
} 