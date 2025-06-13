package org.dasher.speed.taskmanagement.domain;

import jakarta.persistence.*;

import java.util.List;

import org.dasher.speed.taskmanagement.domain.Enums.PersonRole;

@Converter
class PersonRoleConverter implements AttributeConverter<PersonRole, String> {
    @Override
    public String convertToDatabaseColumn(PersonRole attribute) {
        return attribute != null ? attribute.name() : null;
    }

    @Override
    public PersonRole convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            return PersonRole.valueOf(dbData.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Handle old data with proper case
            switch (dbData) {
                case "Patient": return PersonRole.PATIENT;
                case "Doctor": return PersonRole.DOCTOR;
                case "Admin": return PersonRole.ADMIN;
                default: 
                    throw new IllegalArgumentException("Unknown PersonRole: " + dbData);
            }
        }
    }
}

@Entity
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String firstName;
    private String lastName;
    private String phone;
    private String cpf;
    private String gender;

    @Column(name = "marital_status")
    private String maritalStatus;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Convert(converter = PersonRoleConverter.class)
    private PersonRole role;

    @OneToOne(mappedBy = "person", cascade = CascadeType.ALL)
    private Doctor doctor;

    @OneToOne(mappedBy = "person", cascade = CascadeType.ALL)
    private Patient patient;

    @OneToMany(mappedBy = "person_doctor")
    private List<Appointment> appointments_doctor;

    @OneToMany(mappedBy = "person_patient")
    private List<Appointment> appointments_patient;

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public PersonRole getRole() {
        return role;
    }

    public void setRole(PersonRole role) {
        this.role = role;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public List<Appointment> getAppointments_Doctor() {
        return appointments_doctor;
    }

    public void setAppointments_Doctor(List<Appointment> appointments) {
        this.appointments_doctor = appointments;
    }

    public List<Appointment> getAppointments_Patient() {
        return appointments_patient;
    }

    public void setAppointments_Patient(List<Appointment> appointments) {
        this.appointments_patient = appointments;
    }
}