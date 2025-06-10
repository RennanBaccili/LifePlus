package org.dasher.speed.taskmanagement.ui.view;

import org.dasher.speed.base.ui.component.ViewToolbar;
import org.dasher.speed.taskmanagement.domain.Person;
import org.dasher.speed.taskmanagement.domain.Task;
import org.dasher.speed.taskmanagement.service.PersonService;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;


@Route("Doctor")
@PageTitle("Doctors List")
@Menu(order = 0, icon = "vaadin:user", title = "Doctors List")
@PermitAll // When security is enabled, allow all authenticated users
public class DoctorView extends Main {

    private final PersonService personService;
    private final TextField filterText;
    private final Grid<Person> grid;

    public DoctorView(PersonService personService) {
        this.personService = personService;
        this.filterText = new TextField();
        this.grid = new Grid<>();

        setupToolbar();
        configureGrid();
        updateList(); // Load initial data

        setSizeFull();
        addClassNames(LumoUtility.BoxSizing.BORDER, LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN,
                LumoUtility.Padding.MEDIUM, LumoUtility.Gap.SMALL);
    }

    private void setupToolbar() {
        filterText.setPlaceholder("Filter by name...");
        filterText.setAriaLabel("Filter by name");
        filterText.setMaxLength(Task.DESCRIPTION_MAX_LENGTH);
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
        add(grid);
    }

    private void updateList() {
        String searchTerm = filterText.getValue();
        grid.setItems(personService.searchDoctorsByName(searchTerm));
    }
}
