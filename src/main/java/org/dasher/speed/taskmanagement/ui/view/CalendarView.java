package org.dasher.speed.taskmanagement.ui.view;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.vaadin.stefan.fullcalendar.FullCalendar;
import org.vaadin.stefan.fullcalendar.FullCalendarBuilder;
import org.vaadin.stefan.fullcalendar.CalendarViewImpl;
import org.dasher.speed.base.ui.component.ViewToolbar;
import org.dasher.speed.taskmanagement.service.CalendarDataManagerService;
import org.dasher.speed.taskmanagement.ui.components.CalendarEventHandler;

import jakarta.annotation.security.RolesAllowed;

/**
 * Calendar view for managing appointments
 * This view is now focused only on UI setup and coordination between components
 */
@Route("Calendar")
@PageTitle("Minha Agenda | LifePlus")
@Menu(order = 1, icon = "vaadin:calendar", title = "Minha Agenda")
@RolesAllowed({"USER", "ADMIN"})
public class CalendarView extends VerticalLayout {

    private final FullCalendar calendar;
    private final CalendarDataManagerService dataManager;
    private final CalendarEventHandler eventHandler;

    public CalendarView(CalendarDataManagerService dataManager, CalendarEventHandler eventHandler) {
        this.dataManager = dataManager;
        this.eventHandler = eventHandler;
        setSizeFull();
        
        this.calendar = createAndConfigureCalendar();
        setupToolbar();
        setupLayout();
        
        setupEventListeners();
        
        this.dataManager.loadExistingAppointments(calendar);
    }

    /**
     * Creates and configures the FullCalendar component
     */
    private FullCalendar createAndConfigureCalendar() {
        FullCalendar cal = FullCalendarBuilder.create().build();
        cal.changeView(CalendarViewImpl.DAY_GRID_MONTH);
        cal.setSizeFull();
        return cal;
    }
    
    /**
     * Sets up the UI layout
     */
    private void setupLayout() {
        add(calendar);
        setFlexGrow(1, calendar);
    }
    
    /**
     * Sets up event listeners for calendar interactions
     */
    private void setupEventListeners() {
        // Add listener for empty timeslot clicks (create new appointment)
        calendar.addTimeslotClickedListener(event -> 
            eventHandler.handleTimeslotClick(calendar, event, this::refreshCalendar));
        
        // Add listener for existing entry clicks (view/edit appointment)
        calendar.addEntryClickedListener(event -> 
            eventHandler.handleEntryClick(calendar, event, this::refreshCalendar));
    }

    /**
     * Refreshes the calendar view to reflect any changes
     */
    private void refreshCalendar() {
        calendar.getEntryProvider().asInMemory().refreshAll();
    }

    private void setupToolbar() { 
        var toolbar = new ViewToolbar("My Calendar", ViewToolbar.group());
        add(toolbar);
    }

} 