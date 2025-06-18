package org.dasher.speed.taskmanagement.domain.Enums;

public enum PersonRole {
    PATIENT,
    DOCTOR,
    ADMIN;
    
    public String getDisplayName() {
        switch (this) {
            case PATIENT: return "Paciente";
            case DOCTOR: return "Médico";
            case ADMIN: return "Administrador";
            default: return this.name();
        }
    }
}
