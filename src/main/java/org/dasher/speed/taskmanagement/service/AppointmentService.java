package org.dasher.speed.taskmanagement.service;

import org.dasher.speed.taskmanagement.domain.Appointment;
import org.dasher.speed.taskmanagement.domain.Doctor;
import org.dasher.speed.taskmanagement.domain.Person;
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

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Transactional
    public Appointment save(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    @Transactional(readOnly = true)
    public Optional<Appointment> findById(Integer id) {
        return appointmentRepository.findById(id);
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
    public List<Appointment> findByDoctorAndDateRange(Doctor doctor, LocalDateTime startDate, LocalDateTime endDate) {
        return appointmentRepository.findByDoctorAndDateRange(doctor, startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<Appointment> findByDoctorAndDate(Doctor doctor, LocalDateTime date) {
        return appointmentRepository.findByDoctorAndDate(doctor, date);
    }

    @Transactional(readOnly = true)
    public List<Appointment> findUpcomingByDoctor(Doctor doctor) {
        return appointmentRepository.findUpcomingByDoctor(doctor, LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public boolean hasConflictingAppointments(Doctor doctor, LocalDateTime startTime, LocalDateTime endTime, Integer excludeId) {
        List<Appointment> conflicts = appointmentRepository.findConflictingAppointments(
            doctor, startTime, endTime, excludeId != null ? excludeId : -1);
        return !conflicts.isEmpty();
    }

    @Transactional
    public void delete(Integer id) {
        appointmentRepository.deleteById(id);
    }

    @Transactional
    public Appointment updateStatus(Integer appointmentId, Appointment.AppointmentStatus newStatus) {
        Optional<Appointment> appointmentOpt = findById(appointmentId);
        if (appointmentOpt.isPresent()) {
            Appointment appointment = appointmentOpt.get();
            appointment.setStatus(newStatus);
            return save(appointment);
        }
        throw new IllegalArgumentException("Appointment not found with id: " + appointmentId);
    }

    @Transactional(readOnly = true)
    public List<Appointment> findAll() {
        return appointmentRepository.findAll();
    }

    // Método utilitário para validar agendamento
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
        if (appointment.getDoctor() == null) {
            throw new IllegalArgumentException("Médico é obrigatório");
        }
        
        // Verificar conflitos de horário
        Integer excludeId = appointment.getId();
        if (hasConflictingAppointments(appointment.getDoctor(), 
                                     appointment.getAppointmentDate(), 
                                     appointment.getEndDate(), 
                                     excludeId)) {
            throw new IllegalArgumentException("Já existe um agendamento neste horário para o médico");
        }
    }
} 