package org.dasher.speed.taskmanagement.ui.view;

import org.dasher.speed.base.ui.component.ViewToolbar;
import org.dasher.speed.taskmanagement.domain.Person;
import org.dasher.speed.taskmanagement.domain.Doctor;
import org.dasher.speed.taskmanagement.service.PersonService;
import org.dasher.speed.taskmanagement.service.CalendarDataManagerService;
import org.dasher.speed.taskmanagement.ui.components.CalendarDialog;
import org.dasher.speed.taskmanagement.ui.components.CalendarEventHandler;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;


@Route("doctor")
@PageTitle("Doctors List")
@Menu(order = 4, icon = "vaadin:user", title = "Doctors List")
@PermitAll 
public class DoctorView extends VerticalLayout {

    private final PersonService personService;
    private final CalendarDataManagerService dataManager;
    private final CalendarEventHandler eventHandler;
    private final TextField filterText;
    private final Grid<Person> grid;

    public DoctorView(PersonService personService, CalendarDataManagerService dataManager, CalendarEventHandler eventHandler) {
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
        filterText.setPlaceholder("Filter by name...");
        filterText.setAriaLabel("Filter by name");
        filterText.setMinWidth("20em");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());
        add(new ViewToolbar("Doctors List", ViewToolbar.group(filterText)));
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.addClassName("contact-grid");
        grid.addColumn(Person::getFirstName).setHeader("First Name");
        grid.addColumn(Person::getLastName).setHeader("Last Name");
        grid.addColumn(Person::getPhone).setHeader("Phone");
        grid.addColumn(Person::getRole).setHeader("Role");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        
        grid.addComponentColumn(this::createCalendarButton)
            .setHeader("Agenda")
            .setWidth("120px")
            .setFlexGrow(0);
        
        add(grid);
    }

    private Button createCalendarButton(Person doctor) {
        Button calendarButton = new Button();
        calendarButton.setIcon(VaadinIcon.CALENDAR.create());
        calendarButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
        calendarButton.setTooltipText("Ver agenda de " + doctor.getFirstName());
        calendarButton.addClickListener(e -> openDoctorCalendar(doctor));
        return calendarButton;
    }

    private void updateList() {
        String searchTerm = filterText.getValue();
        grid.setItems(personService.searchDoctorsByName(searchTerm));
    }
    
    private void openDoctorCalendar(Person selectedDoctor) {
        try {
            Doctor doctor = validateDoctor(selectedDoctor);
            if (doctor == null) return;
            
            CalendarDialog dialog = new CalendarDialog(doctor, dataManager, eventHandler);
            dialog.open();
            
        } catch (Exception e) {
            showErrorNotification("Erro ao abrir agenda do médico", e.getMessage());
        }
    }

    private Doctor validateDoctor(Person selectedDoctor) {
        Doctor doctor = selectedDoctor.getDoctor();
        if (doctor == null) {
            showErrorNotification("Validação", "Esta pessoa não possui perfil de médico");
            return null;
        }
        return doctor;
    }

    private void showErrorNotification(String title, String message) {
        String fullMessage = String.format("%s: %s", title, message);
        Notification.show(fullMessage, 3000, Notification.Position.MIDDLE);
    }
}
