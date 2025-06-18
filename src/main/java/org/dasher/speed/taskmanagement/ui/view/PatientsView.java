package org.dasher.speed.taskmanagement.ui.view;

import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import jakarta.annotation.security.RolesAllowed;

import org.dasher.speed.base.ui.component.ViewToolbar;
import org.dasher.speed.taskmanagement.domain.Person;
import org.dasher.speed.taskmanagement.domain.Enums.PersonRole;
import org.dasher.speed.taskmanagement.service.CalendarDataManagerService;
import org.dasher.speed.taskmanagement.service.PersonService;
import org.dasher.speed.taskmanagement.ui.components.CalendarEventHandler;
import org.dasher.speed.taskmanagement.ui.components.CalendarDialog;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

@Route("patients")
@PageTitle("Pacientes | LifePlus")
@Menu(order = 1, icon = "vaadin:user-heart", title = "Patients")
@RolesAllowed({"USER", "ADMIN"})
public class PatientsView extends VerticalLayout {
    
    private final PersonService personService;
    private final CalendarDataManagerService dataManager;
    private final CalendarEventHandler eventHandler;
    private final TextField filterText;
    private final Grid<Person> grid;

    public PatientsView(PersonService personService, CalendarDataManagerService dataManager, CalendarEventHandler eventHandler) {
        this.personService = personService;
        this.dataManager = dataManager;
        this.eventHandler = eventHandler;
        this.filterText = new TextField();
        this.grid = new Grid<>();

        setupToolbar();
        configureGrid();
        updateList(); 

        setSizeFull();
        addClassNames(LumoUtility.BoxSizing.BORDER, LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN,
                LumoUtility.Padding.MEDIUM, LumoUtility.Gap.SMALL);
    }

    private void setupToolbar() {
        filterText.setPlaceholder("Filtrar por nome...");
        filterText.setAriaLabel("Filtrar por nome");
        filterText.setMinWidth("20em");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());
        add(new ViewToolbar("Lista de Pacientes", ViewToolbar.group(filterText)));
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.addClassName("contact-grid");
        grid.addColumn(Person::getFirstName).setHeader("Nome");
        grid.addColumn(Person::getLastName).setHeader("Sobrenome");
        grid.addColumn(Person::getPhone).setHeader("Telefone");
        grid.addColumn(person -> person.getRole().getDisplayName()).setHeader("Tipo");
        
        // Add calendar action column
        grid.addComponentColumn(this::createCalendarButton)
            .setHeader("Agenda")
            .setWidth("120px")
            .setFlexGrow(0);
        
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        
        add(grid);
    }

    private Button createCalendarButton(Person patient) {
        Button calendarButton = new Button();
        calendarButton.setIcon(VaadinIcon.CALENDAR.create());
        calendarButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
        calendarButton.setTooltipText("Ver agenda de " + patient.getFirstName());
        calendarButton.addClickListener(e -> openPatientCalendar(patient));
        return calendarButton;
    }

    private void updateList() {
        String searchTerm = filterText.getValue();
        grid.setItems(personService.searchPatientsByName(searchTerm));
    }
    
    private void openPatientCalendar(Person selectedPatient) {
        try {
            if (selectedPatient.getRole() == PersonRole.DOCTOR) {
                showErrorNotification("Validação", "Esta pessoa é um médico, não um paciente");
                return;
            }
            
            CalendarDialog dialog = new CalendarDialog(selectedPatient, dataManager, eventHandler);
            dialog.open();
            
        } catch (Exception e) {
            showErrorNotification("Erro ao abrir agenda do paciente", e.getMessage());
        }
    }

    private void showErrorNotification(String title, String message) {
        String fullMessage = String.format("%s: %s", title, message);
        Notification.show(fullMessage, 3000, Notification.Position.MIDDLE);
    }
}
