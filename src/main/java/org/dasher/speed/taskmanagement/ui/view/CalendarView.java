package org.dasher.speed.taskmanagement.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.vaadin.stefan.fullcalendar.FullCalendar;
import org.vaadin.stefan.fullcalendar.FullCalendarBuilder;
import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.CalendarViewImpl;
import org.vaadin.stefan.fullcalendar.Timezone;
import org.vaadin.stefan.fullcalendar.TimeslotClickedEvent;
import org.vaadin.stefan.fullcalendar.EntryClickedEvent;

import org.dasher.speed.base.ui.component.ViewToolbar;
import org.dasher.speed.taskmanagement.domain.Appointment;
import org.dasher.speed.taskmanagement.domain.Doctor;
import org.dasher.speed.taskmanagement.domain.Person;
import org.dasher.speed.taskmanagement.domain.User;
import org.dasher.speed.taskmanagement.domain.Enums.PersonRole;
import org.dasher.speed.taskmanagement.service.AppointmentService;
import org.dasher.speed.taskmanagement.service.PersonService;
import org.dasher.speed.taskmanagement.service.UserService;
import org.dasher.speed.taskmanagement.security.SecurityService;
import org.dasher.speed.taskmanagement.ui.components.AppointmentDialog;

import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;

@Route("Calendar")
@PageTitle("Minha Agenda | LifePlus")
@Menu(order = 1, icon = "vaadin:calendar", title = "Minha Agenda")
@RolesAllowed({"USER", "ADMIN"})
public class CalendarView extends VerticalLayout {

    private final FullCalendar calendar;
    private final AppointmentService appointmentService;
    private final PersonService personService;
    private final UserService userService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final Map<Entry, Integer> entryToAppointmentId = new HashMap<>();

    public CalendarView(AppointmentService appointmentService, PersonService personService, UserService userService) {
        this.appointmentService = appointmentService;
        this.personService = personService;
        this.userService = userService;
        
        setSizeFull();
        calendar = FullCalendarBuilder.create().build();

        //calendar.setLocale("pt-br"); // Ativa idioma português
        calendar.changeView(CalendarViewImpl.DAY_GRID_MONTH); // Exibe semana

        calendar.setSizeFull();
        add(calendar);
        setFlexGrow(1, calendar);

        // Adiciona listener para clique em uma data vazia
        calendar.addTimeslotClickedListener(this::handleDateClick);
        
        // Adiciona listener para clique em eventos existentes
        calendar.addEntryClickedListener(this::handleEntryClick);

        // Carrega os agendamentos existentes
        loadExistingAppointments();
    }

    private void loadExistingAppointments() {
        try {
            // Busca todos os agendamentos
            List<Appointment> appointments = appointmentService.findAll();
            
            // Adiciona cada agendamento ao calendário
            for (Appointment appointment : appointments) {
                addAppointmentToCalendar(appointment);
            }
        } catch (Exception e) {
            Notification.show("Erro ao carregar agendamentos: " + e.getMessage(), 
                3000, Notification.Position.MIDDLE);
        }
    }

    private Entry createCalendarEntry(Appointment appointment) {
        Entry entry = new Entry();
        
        // Informações básicas
        entry.setTitle(appointment.getTitle());
        entry.setStart(appointment.getAppointmentDate());
        entry.setEnd(appointment.getEndDate());
        entry.setColor("#2196F3"); // Cor padrão para consultas
        
        // Adiciona informações detalhadas na descrição
        StringBuilder description = new StringBuilder();
        description.append("Médico: ").append(appointment.getDoctor().getPerson().getFirstName())
                  .append(" ").append(appointment.getDoctor().getPerson().getLastName());
        
        if (appointment.getPerson() != null) {
            description.append("\nAgendado por: ").append(appointment.getPerson().getFirstName())
                      .append(" ").append(appointment.getPerson().getLastName());
        }
        
        if (appointment.getDescription() != null) {
            description.append("\n").append(appointment.getDescription());
        }
        
        entry.setDescription(description.toString());
        
        // Guarda o ID do appointment para referência
        entryToAppointmentId.put(entry, appointment.getId());
        
        return entry;
    }

    private void addAppointmentToCalendar(Appointment appointment) {
        Entry entry = createCalendarEntry(appointment);
        calendar.getEntryProvider().asInMemory().addEntry(entry);
    }

    private List<Person> getDoctors() {
        return personService.findAllDoctors().stream()
            .collect(Collectors.toList());
    }

    private Person getCurrentPerson() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = (User) userService.loadUserByUsername(username);
        return personService.findByUser(currentUser)
            .orElseThrow(() -> new IllegalStateException("Usuário atual não possui Person associada"));
    }

    private void handleDateClick(TimeslotClickedEvent event) {
        LocalDateTime clickedDateTime = event.getDateTime();
        List<Person> doctors = getDoctors(); // Busca todas as pessoas que são médicos
        
        AppointmentDialog dialog = new AppointmentDialog(
            "Novo Agendamento",
            "Criar agendamento para " + clickedDateTime.format(DATE_FORMATTER),
            doctors,
            clickedDateTime
        );
        
        // Configura os botões para novo agendamento
        dialog.showEditButton(false);
        dialog.showCancelButton(false);
        dialog.showSaveButton(true);
        
        // Configura ação de salvar
        dialog.onSave(() -> {
            try {
                Person selectedDoctor = dialog.getSelectedDoctor();
                if (selectedDoctor == null) {
                    throw new IllegalArgumentException("Por favor, selecione um médico");
                }

                // Verifica se o Doctor já existe na Person
                Doctor doctor = selectedDoctor.getDoctor();
                if (doctor == null) {
                    throw new IllegalArgumentException("Médico selecionado não possui cadastro como doutor");
                }

                // Obtém a Person do usuário atual
                Person currentPerson = getCurrentPerson();
                
                // Cria um novo agendamento com os dados do formulário
                Appointment appointment = new Appointment();
                appointment.setTitle(dialog.getTitle());
                appointment.setAppointmentDate(dialog.getStartDateTime());
                appointment.setEndDate(dialog.getEndDateTime());
                appointment.setStatus(Appointment.AppointmentStatus.SCHEDULED);
                appointment.setDoctor(doctor); // Médico selecionado
                appointment.setPerson(currentPerson); // Pessoa que está agendando
                
                // Se a pessoa que está agendando é um paciente, adiciona informações extras
                if (currentPerson.getRole() == PersonRole.PATIENT) {
                    appointment.setDescription("Paciente: " + currentPerson.getFirstName() + " " + currentPerson.getLastName());
                }
                
                // Valida e salva o agendamento
                appointmentService.validateAppointment(appointment);
                Appointment savedAppointment = appointmentService.save(appointment);
                
                // Adiciona ao calendário
                addAppointmentToCalendar(savedAppointment);
                
                Notification.show("Agendamento criado com sucesso!", 3000, Notification.Position.MIDDLE);
            } catch (Exception e) {
                Notification.show("Erro ao criar agendamento: " + e.getMessage(), 
                    3000, Notification.Position.MIDDLE);
            }
        });
        
        dialog.open();
    }

    private void handleEntryClick(EntryClickedEvent event) {
        Entry clickedEntry = event.getEntry();
        Integer appointmentId = entryToAppointmentId.get(clickedEntry);
        
        if (appointmentId == null) {
            Notification.show("Erro ao carregar agendamento", 3000, Notification.Position.MIDDLE);
            return;
        }

        try {
            Optional<Appointment> appointmentOpt = appointmentService.findById(appointmentId);
            if (appointmentOpt.isEmpty()) {
                Notification.show("Agendamento não encontrado", 3000, Notification.Position.MIDDLE);
                return;
            }

            Appointment appointment = appointmentOpt.get();
            Doctor doctor = appointment.getDoctor();
            Person patient = appointment.getPerson();

            StringBuilder content = new StringBuilder();
            content.append("Detalhes do Agendamento\n\n");
            
            if (patient != null) {
                content.append("Paciente: ").append(patient.getFirstName())
                       .append(" ").append(patient.getLastName()).append("\n");
            }

            if (appointment.getDescription() != null && !appointment.getDescription().isEmpty()) {
                content.append("\nObservações: ").append(appointment.getDescription()).append("\n");
            }

            content.append("\nStatus: ").append(appointment.getStatus().getDisplayName());
            
            AppointmentDialog dialog = new AppointmentDialog(
                "Detalhes do Agendamento",
                content.toString(),
                getDoctors(),
                appointment.getAppointmentDate(),
                appointment  // Passa o appointment existente
            );
            
            dialog.showEditButton(true);  
            dialog.showCancelButton(true);  
            dialog.showSaveButton(false);  
            
            dialog.onEdit(() -> {
                dialog.showSaveButton(true);
                dialog.showEditButton(false);
                
                // Habilita a edição dos campos de data/hora
                dialog.onSave(() -> {
                    try {
                        // Atualiza apenas as datas do agendamento
                        appointment.setAppointmentDate(dialog.getStartDateTime());
                        appointment.setEndDate(dialog.getEndDateTime());
                        
                        // Valida e salva o agendamento
                        appointmentService.validateAppointment(appointment);
                        Appointment savedAppointment = appointmentService.save(appointment);
                        
                        // Atualiza o calendário
                        calendar.getEntryProvider().asInMemory().removeEntry(clickedEntry);
                        addAppointmentToCalendar(savedAppointment);
                        
                        Notification.show("Agendamento atualizado com sucesso!", 3000, Notification.Position.MIDDLE);
                    } catch (Exception e) {
                        Notification.show("Erro ao atualizar agendamento: " + e.getMessage(), 
                            3000, Notification.Position.MIDDLE);
                    }
                });
            });
            
            dialog.onCancel(() -> {
                try {
                    // Remove do banco de dados e do calendário
                    appointmentService.delete(appointmentId);
                    calendar.getEntryProvider().asInMemory().removeEntry(clickedEntry);
                    entryToAppointmentId.remove(clickedEntry);
                    Notification.show("Agendamento cancelado!", 3000, Notification.Position.MIDDLE);
                } catch (Exception e) {
                    Notification.show("Erro ao cancelar agendamento: " + e.getMessage(), 
                        3000, Notification.Position.MIDDLE);
                }
            });
            
            dialog.open();
        } catch (Exception e) {
            Notification.show("Erro ao carregar detalhes do agendamento: " + e.getMessage(), 
                3000, Notification.Position.MIDDLE);
        }
    }
} 