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
    
    @Query("SELECT DISTINCT a FROM Appointment a " +
           "LEFT JOIN FETCH a.person_doctor pd " +
           "LEFT JOIN FETCH a.person_patient pp " +
           "WHERE pd IN (SELECT d.person FROM Doctor d WHERE d = :doctor) " +
           "ORDER BY a.appointmentDate")
    List<Appointment> findByDoctor(@Param("doctor") Doctor doctor);
    
    // Buscar agendamentos onde uma pessoa é paciente
    @Query("SELECT DISTINCT a FROM Appointment a " +
           "LEFT JOIN FETCH a.person_doctor pd " +
           "LEFT JOIN FETCH a.person_patient pp " +
           "WHERE a.person_patient = :person " +
           "ORDER BY a.appointmentDate")
    List<Appointment> findByPerson(@Param("person") Person person);
    
    // Buscar agendamentos por médico em um período específico
    @Query("SELECT a FROM Appointment a " +
           "WHERE a.person_doctor IN (SELECT d.person FROM Doctor d WHERE d = :doctor) " +
           "AND a.appointmentDate >= :startDate AND a.appointmentDate <= :endDate " +
           "ORDER BY a.appointmentDate")
    List<Appointment> findByDoctorAndDateRange(@Param("doctor") Doctor doctor,
                                               @Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT a FROM Appointment a " +
           "LEFT JOIN FETCH a.person_patient pp " +
           "WHERE pp IS NOT NULL " +
           "AND pp.role = org.dasher.speed.taskmanagement.domain.Enums.PersonRole.PATIENT " +
           "AND (LOWER(pp.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(pp.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Appointment> searchAppointmentsByPatient(@Param("searchTerm") String searchTerm);

    // Buscar agendamentos por médico em uma data específica
    @Query("SELECT a FROM Appointment a WHERE a.person_doctor = :person_doctor " +
           "AND DATE(a.appointmentDate) = DATE(:date) " +
           "ORDER BY a.appointmentDate")
    List<Appointment> findByDoctorAndDate(@Param("person_doctor") Person person_doctor,
                                          @Param("date") LocalDateTime date);
    
    // Verificar conflitos de horário para um médico
    @Query("SELECT a FROM Appointment a WHERE a.person_doctor = :person_doctor " +
           "AND a.id != :excludeId " +
           "AND ((a.appointmentDate <= :startTime AND a.endDate > :startTime) " +
           "OR (a.appointmentDate < :endTime AND a.endDate >= :endTime) " +
           "OR (a.appointmentDate >= :startTime AND a.endDate <= :endTime))")
    List<Appointment> findConflictingAppointments(@Param("person_doctor") Person person_doctor,
                                                   @Param("startTime") LocalDateTime startTime,
                                                   @Param("endTime") LocalDateTime endTime,
                                                   @Param("excludeId") Integer excludeId);
    
    // Buscar próximos agendamentos por médico
    @Query("SELECT a FROM Appointment a WHERE a.person_doctor = :person_doctor " +
           "AND a.appointmentDate >= :now " +
           "AND a.status NOT IN (org.dasher.speed.taskmanagement.domain.Appointment$AppointmentStatus.CANCELLED, " +
           "org.dasher.speed.taskmanagement.domain.Appointment$AppointmentStatus.COMPLETED) " +
           "ORDER BY a.appointmentDate")
    List<Appointment> findUpcomingByDoctor(@Param("person_doctor") Person person_doctor,
                                           @Param("now") LocalDateTime now);

    @Query("SELECT a FROM Appointment a " +
           "LEFT JOIN FETCH a.person_doctor pd " +
           "LEFT JOIN FETCH a.person_patient pp " +
           "ORDER BY a.appointmentDate")
    List<Appointment> findAllWithDetails();

    @Query("SELECT a FROM Appointment a " +
           "LEFT JOIN FETCH a.person_doctor pd " +
           "LEFT JOIN FETCH a.person_patient pp " +
           "WHERE a.id = :id")
    Optional<Appointment> findByIdWithDetails(@Param("id") long id);
} 