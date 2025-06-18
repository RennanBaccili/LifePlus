package org.dasher.speed.taskmanagement.service;

import org.dasher.speed.taskmanagement.domain.Appointment;
import org.dasher.speed.taskmanagement.domain.Doctor;
import org.dasher.speed.taskmanagement.domain.NotificationMessage;
import org.dasher.speed.taskmanagement.domain.Person;
import org.dasher.speed.taskmanagement.domain.Appointment.AppointmentStatus;
import org.dasher.speed.taskmanagement.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final NotificationMessageService notificationMessageService;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository, NotificationMessageService notifcationMessageService) {
         this.appointmentRepository = appointmentRepository;
         this.notificationMessageService = notifcationMessageService;
    }

    @Transactional
    public Appointment save(Appointment appointment) {
        var appointmentSaved = appointmentRepository.save(appointment);
        return appointmentSaved;
    }

    @Transactional
    public Appointment updateAppointment(Appointment appointment) {
        var appointmentSaved = appointmentRepository.save(appointment);
        return appointmentSaved;
    }

    @Transactional(readOnly = true)
    public Optional<Appointment> getAppointmentById(long id) {
        return appointmentRepository.findByIdWithDetails(id);
    }

    @Transactional(readOnly = true)
    public List<Appointment> findByDoctor(Doctor doctor) {
        return appointmentRepository.findByDoctor(doctor);
    }

    @Transactional(readOnly = true)
    public List<Appointment> findByPerson(Person person) {
        return appointmentRepository.findByPerson(person);
    }

    @Transactional(readOnly = true)
    public List<Appointment> searchAppointmentsByPatient(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return appointmentRepository.findAll();
        }
        return appointmentRepository.searchAppointmentsByPatient(searchTerm);
    }

    @Transactional(readOnly = true)
    public List<Appointment> findByDoctorAndDateRange(Doctor doctor, LocalDateTime startDate, LocalDateTime endDate) {
        return appointmentRepository.findByDoctorAndDateRange(doctor, startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<Appointment> findByDoctorAndDate(Person person_doctor, LocalDateTime date) {
        return appointmentRepository.findByDoctorAndDate(person_doctor, date);
    }

    @Transactional(readOnly = true)
    public List<Appointment> findUpcomingByDoctor(Person person_doctor) {
        return appointmentRepository.findUpcomingByDoctor(person_doctor, LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public boolean hasConflictingAppointments(Person person_doctor, LocalDateTime startTime, LocalDateTime endTime, Integer excludeId) {
        List<Appointment> conflicts = appointmentRepository.findConflictingAppointments(
            person_doctor, startTime, endTime, excludeId != null ? excludeId : -1);
        return !conflicts.isEmpty();
    }

    @Transactional
    public void delete(Integer id) {
        appointmentRepository.deleteById(id);
    }

    @Transactional
    public Appointment updateStatus(Integer appointmentId, Appointment.AppointmentStatus newStatus) {
        Optional<Appointment> appointmentOpt = getAppointmentById(appointmentId);
        if (appointmentOpt.isPresent()) {
            Appointment appointment = appointmentOpt.get();
            appointment.setStatus(newStatus);
             var appointmentSaved = save(appointment);
            notificationMessageService.sendNotificationByAppointment(appointmentSaved);
            return appointmentSaved;
        }
        throw new IllegalArgumentException("Appointment not found with id: " + appointmentId);
    }

    @Transactional(readOnly = true)
    public List<Appointment> findAll() {
        return appointmentRepository.findAllWithDetails();
    }

    public void validateAppointment(Appointment appointment) throws IllegalArgumentException {
        if (appointment.getAppointmentDate() == null) {
            throw new IllegalArgumentException("Data do agendamento é obrigatória");
        }
        if (appointment.getEndDate() == null) {
            throw new IllegalArgumentException("Data de fim é obrigatória");
        }
        if (appointment.getAppointmentDate().isAfter(appointment.getEndDate())) {
            throw new IllegalArgumentException("Data de início deve ser anterior à data de fim");
        }
        if (appointment.getPersonDoctor() == null) {
            throw new IllegalArgumentException("Médico é obrigatório");
        }
        
        Integer excludeId = appointment.getId();
        if (hasConflictingAppointments(appointment.getPersonDoctor(), 
                                     appointment.getAppointmentDate(), 
                                     appointment.getEndDate(), 
                                     excludeId)) {
            throw new IllegalArgumentException("Já existe um agendamento neste horário para o médico");
        }
    }

    public Appointment acceptSchedule(boolean isAccepted, NotificationMessage notificationMessage){
        var appointment = getAppointmentById(notificationMessage.getAppointmentId());
        if (appointment.isPresent()) {
            if (isAccepted) {
                appointment.get().setStatus(AppointmentStatus.SCHEDULED);
            } else {
                appointment.get().setStatus(AppointmentStatus.CANCELLED);
            }
            var updatedAppointment = updateAppointment(appointment.get());
            notificationMessageService.sendNotificationByAppointment(updatedAppointment);
            return updatedAppointment;
        }
        return null;
    }
} 