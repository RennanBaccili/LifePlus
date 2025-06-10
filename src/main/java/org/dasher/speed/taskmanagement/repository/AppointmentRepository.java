package org.dasher.speed.taskmanagement.repository;

import org.dasher.speed.taskmanagement.domain.Appointment;
import org.dasher.speed.taskmanagement.domain.Doctor;
import org.dasher.speed.taskmanagement.domain.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    
    // Buscar agendamentos por médico
    List<Appointment> findByDoctor(Doctor doctor);
    
    // Buscar agendamentos onde uma pessoa é paciente
    List<Appointment> findByPerson(Person person);
    
    // Buscar agendamentos por médico em um período específico
    @Query("SELECT a FROM Appointment a WHERE a.doctor = :doctor " +
           "AND a.appointmentDate >= :startDate AND a.appointmentDate <= :endDate " +
           "ORDER BY a.appointmentDate")
    List<Appointment> findByDoctorAndDateRange(@Param("doctor") Doctor doctor,
                                               @Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate);
    
    // Buscar agendamentos por médico em uma data específica
    @Query("SELECT a FROM Appointment a WHERE a.doctor = :doctor " +
           "AND DATE(a.appointmentDate) = DATE(:date) " +
           "ORDER BY a.appointmentDate")
    List<Appointment> findByDoctorAndDate(@Param("doctor") Doctor doctor,
                                          @Param("date") LocalDateTime date);
    
    // Verificar conflitos de horário para um médico
    @Query("SELECT a FROM Appointment a WHERE a.doctor = :doctor " +
           "AND a.id != :excludeId " +
           "AND ((a.appointmentDate <= :startTime AND a.endDate > :startTime) " +
           "OR (a.appointmentDate < :endTime AND a.endDate >= :endTime) " +
           "OR (a.appointmentDate >= :startTime AND a.endDate <= :endTime))")
    List<Appointment> findConflictingAppointments(@Param("doctor") Doctor doctor,
                                                   @Param("startTime") LocalDateTime startTime,
                                                   @Param("endTime") LocalDateTime endTime,
                                                   @Param("excludeId") Integer excludeId);
    
    // Buscar próximos agendamentos por médico
    @Query("SELECT a FROM Appointment a WHERE a.doctor = :doctor " +
           "AND a.appointmentDate >= :now " +
           "AND a.status NOT IN (org.dasher.speed.taskmanagement.domain.Appointment$AppointmentStatus.CANCELLED, " +
           "org.dasher.speed.taskmanagement.domain.Appointment$AppointmentStatus.COMPLETED) " +
           "ORDER BY a.appointmentDate")
    List<Appointment> findUpcomingByDoctor(@Param("doctor") Doctor doctor,
                                           @Param("now") LocalDateTime now);

    @Query("SELECT a FROM Appointment a " +
           "LEFT JOIN FETCH a.doctor d " +
           "LEFT JOIN FETCH d.person dp " +
           "LEFT JOIN FETCH a.person p " +
           "ORDER BY a.appointmentDate")
    List<Appointment> findAllWithDetails();

    @Query("SELECT a FROM Appointment a " +
           "LEFT JOIN FETCH a.doctor d " +
           "LEFT JOIN FETCH d.person dp " +
           "LEFT JOIN FETCH a.person p " +
           "WHERE a.id = :id")
    Optional<Appointment> findByIdWithDetails(@Param("id") Integer id);
} 