package org.dasher.speed.taskmanagement.ui.view;

import org.dasher.speed.base.ui.component.ViewToolbar;
import org.dasher.speed.taskmanagement.domain.Person;
import org.dasher.speed.taskmanagement.domain.Appointment;
import org.dasher.speed.taskmanagement.service.AppointmentService;
import org.dasher.speed.taskmanagement.service.CalendarDataManagerService;
import org.dasher.speed.taskmanagement.service.PersonService;
import org.dasher.speed.taskmanagement.ui.components.CalendarEventHandler;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import java.time.format.DateTimeFormatter;


@Route("appointments")
@PageTitle("Appointments  | LifePlus")
@Menu(order = 2, icon = "vaadin:user", title = "Appointments")
@PermitAll 
public class AppoitmentsView extends VerticalLayout {

    private final AppointmentService appointmentService;
    private final CalendarDataManagerService dataManager;
    private final CalendarEventHandler eventHandler;
    private final PersonService personService;
    private final TextField filterText;
    private final Grid<Appointment> grid;

    public AppoitmentsView(AppointmentService appointmentService, CalendarDataManagerService dataManager, CalendarEventHandler eventHandler, PersonService personService) {
        this.appointmentService = appointmentService;
        this.dataManager = dataManager;
        this.eventHandler = eventHandler;
        this.personService = personService;
        this.filterText = new TextField();
        this.grid = new Grid<>();

        setupToolbar();
        configureGrid();
        setupEventListeners();
        updateList(); 

        setSizeFull();
        addClassNames(LumoUtility.BoxSizing.BORDER, LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN,
                LumoUtility.Padding.MEDIUM, LumoUtility.Gap.SMALL);
    }

    private void setupToolbar() {
        filterText.setPlaceholder("Filtrar por nome do paciente...");
        filterText.setAriaLabel("Filtrar por nome do paciente");
        filterText.setMinWidth("20em");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());
        add(new ViewToolbar("Lista de Agendamentos", ViewToolbar.group(filterText)));
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.addClassName("contact-grid");
        grid.addColumn(appointment -> {
            Person doctor = appointment.getPersonDoctor();
            return doctor != null ? doctor.getFirstName() + " " + doctor.getLastName() : "N/A";
        }).setHeader("Médico");
        
        grid.addColumn(appointment -> {
            Person patient = appointment.getPersonPatient();
            return patient != null ? patient.getFirstName() + " " + patient.getLastName() : "Paciente externo";
        }).setHeader("Paciente");
        
        grid.addColumn(appointment -> {
            return appointment.getAppointmentDate() != null ? 
                appointment.getAppointmentDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "N/A";
        }).setHeader("Data e Hora Inicial");
        
        grid.addColumn(appointment -> {
            return appointment.getEndDate() != null ? 
                appointment.getEndDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "N/A";
        }).setHeader("Data e Hora Final");

        grid.addColumn(appointment -> {
            return appointment.getStatus() != null ? 
                appointment.getStatus().getDisplayName() : "N/A";
        }).setHeader("Status");
        
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        
        add(grid);
    }

    private void setupEventListeners() {
        // Add listener for existing entry clicks (view/edit appointment)
        grid.addItemClickListener(event -> 
            eventHandler.handleEntryGridClick(grid, event, this::updateList));
    }

    private void updateList() {
        try {	
            String searchTerm = filterText.getValue();
            Person currentPerson = personService.getCurrentPerson();
            
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                // Se não há filtro, mostra appointments relacionados ao usuário atual
                grid.setItems(appointmentService.findRelatedToPerson(currentPerson));
            } else {
                // Se há filtro, busca por nome do paciente nos appointments do usuário
                grid.setItems(appointmentService.findRelatedToPerson(currentPerson).stream()
                    .filter(appointment -> {
                        Person patient = appointment.getPersonPatient();
                        if (patient == null) return false;
                        String fullName = patient.getFirstName() + " " + patient.getLastName();
                        return fullName.toLowerCase().contains(searchTerm.toLowerCase());
                    })
                    .toList());
            }
        } catch (Exception e) {
            showErrorNotification("Erro ao atualizar lista de agendamentos", e.getMessage());
        }
    }

    private void showErrorNotification(String title, String message) {
        String fullMessage = String.format("%s: %s", title, message);
        Notification.show(fullMessage, 3000, Notification.Position.MIDDLE);
    }
}
