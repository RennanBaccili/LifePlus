package org.dasher.speed.taskmanagement.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(name = "appointment_date")
    private LocalDateTime appointmentDate;

    @NotNull
    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "title")
    private String title;

    @Column(name = "description", length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AppointmentStatus status = AppointmentStatus.SCHEDULED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    private Person person; // Qualquer pessoa pode agendar (médico, paciente, etc.)

    @Column(name = "external_patient_name")
    private String externalPatientName; // Para pessoas não cadastradas no sistema

    @Column(name = "external_patient_phone")
    private String externalPatientPhone;

    // Construtores
    public Appointment() {}

    public Appointment(LocalDateTime appointmentDate, LocalDateTime endDate, 
                      String title, Doctor doctor) {
        this.appointmentDate = appointmentDate;
        this.endDate = endDate;
        this.title = title;
        this.doctor = doctor;
    }

    // Getters e Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDateTime appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getExternalPatientName() {
        return externalPatientName;
    }

    public void setExternalPatientName(String externalPatientName) {
        this.externalPatientName = externalPatientName;
    }

    public String getExternalPatientPhone() {
        return externalPatientPhone;
    }

    public void setExternalPatientPhone(String externalPatientPhone) {
        this.externalPatientPhone = externalPatientPhone;
    }

    // Método utilitário para obter o nome da pessoa agendada
    public String getDisplayPatientName() {
        if (person != null) {
            return person.getFirstName() + " " + person.getLastName();
        }
        return externalPatientName != null ? externalPatientName : "Pessoa não informada";
    }

    // Enum para status do agendamento
    public enum AppointmentStatus {
        SCHEDULED("Agendado"),
        CONFIRMED("Confirmado"),
        IN_PROGRESS("Em andamento"),
        COMPLETED("Concluído"),
        CANCELLED("Cancelado"),
        NO_SHOW("Não compareceu");

        private final String displayName;

        AppointmentStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
} 