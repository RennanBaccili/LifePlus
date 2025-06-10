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

import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Route("Calendar")
@PageTitle("Minha Agenda | LifePlus")
@Menu(order = 1, icon = "vaadin:calendar", title = "Minha Agenda")
@RolesAllowed({"USER", "ADMIN"})
public class CalendarView extends VerticalLayout {

    private final AppointmentService appointmentService;
    private final PersonService personService;
    private final UserService userService;
    private final SecurityService securityService;

    private FullCalendar calendar;
    private Doctor currentDoctor;
    private Person currentPerson;

    private final Button newAppointmentButton = new Button("Novo Agendamento");

    public CalendarView(AppointmentService appointmentService, 
                       PersonService personService,
                       UserService userService,
                       SecurityService securityService) {
        this.appointmentService = appointmentService;
        this.personService = personService;
        this.userService = userService;
        this.securityService = securityService;

        setupLayout();
        loadCurrentDoctor();
        setupCalendar();
        loadAppointments();
    }

    private void setupLayout() {
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        var toolbar = new ViewToolbar("Minha Agenda");
        add(toolbar);

        setupControlButtons();
    }

    private void setupControlButtons() {
        newAppointmentButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newAppointmentButton.addClickListener(e -> openNewAppointmentDialog());

        HorizontalLayout buttonsLayout = new HorizontalLayout();
        buttonsLayout.add(newAppointmentButton);
        buttonsLayout.setSpacing(true);
        add(buttonsLayout);
    }

    private void loadCurrentDoctor() {
        try {
            String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
            User currentUser = (User) userService.loadUserByUsername(currentUsername);
            
            if (currentUser != null) {
                Optional<Person> personOpt = personService.findByUser(currentUser);
                if (personOpt.isPresent()) {
                    currentPerson = personOpt.get();
                    
                    // Se a pessoa é médico, permite criar agendamentos
                    if (currentPerson.getRole() == PersonRole.DOCTOR && currentPerson.getDoctor() != null) {
                        currentDoctor = currentPerson.getDoctor();
                        newAppointmentButton.setText("Novo Agendamento (Como Médico)");
                    } else {
                        // Qualquer pessoa pode ver seus agendamentos, mas não criar como médico
                        newAppointmentButton.setVisible(false);
                    }
                } else {
                    showErrorNotification("Perfil de pessoa não encontrado");
                    newAppointmentButton.setEnabled(false);
                }
            } else {
                showErrorNotification("Usuário não encontrado");
                newAppointmentButton.setEnabled(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorNotification("Erro ao carregar dados: " + e.getMessage());
        }
    }

    private void setupCalendar() {
        // Criar calendário usando o builder
        calendar = FullCalendarBuilder.create().build();
        calendar.setSizeFull();
        
        // Configurar visualização inicial
        calendar.changeView(CalendarViewImpl.DAY_GRID_MONTH);
        
        // Adicionar listener para clique em entradas
        calendar.addEntryClickedListener(event -> {
            Entry clickedEntry = event.getEntry();
            if (clickedEntry != null && clickedEntry.getCustomProperty("appointmentId") != null) {
                String appointmentIdStr = clickedEntry.getCustomProperty("appointmentId").toString();
                try {
                    Integer appointmentId = Integer.parseInt(appointmentIdStr);
                    openEditAppointmentDialog(appointmentId);
                } catch (NumberFormatException ex) {
                    showErrorNotification("Erro ao abrir agendamento");
                }
            }
        });

        // Adicionar listener para clique em datas vazias
        calendar.addTimeslotClickedListener(event -> {
            LocalDate clickedDate = event.getDate();
            openNewAppointmentDialog(clickedDate);
        });

        add(calendar);
    }

    private void loadAppointments() {
        if (currentPerson == null) {
            return;
        }

        try {
            List<Appointment> allAppointments = new java.util.ArrayList<>();
            
            // Se for médico, carregar agendamentos onde é o médico
            if (currentDoctor != null) {
                List<Appointment> doctorAppointments = appointmentService.findByDoctor(currentDoctor);
                allAppointments.addAll(doctorAppointments);
            }
            
            // Carregar agendamentos onde a pessoa é paciente (qualquer pessoa pode ter)
            List<Appointment> personAppointments = appointmentService.findByPerson(currentPerson);
            allAppointments.addAll(personAppointments);
            
            // Remover duplicatas (caso a pessoa seja médico e paciente do mesmo agendamento - improvável mas possível)
            allAppointments = allAppointments.stream().distinct().collect(java.util.stream.Collectors.toList());
            
            String appointmentType = currentDoctor != null ? 
                "agendamentos (como médico e paciente)" : 
                "agendamentos (como paciente)";
                
            showSuccessNotification("Encontrados " + allAppointments.size() + " " + appointmentType);
            
            // TODO: Implementar carregamento dos agendamentos no calendário
            // A API do FullCalendar pode variar dependendo da versão
            // Use calendar.add(entry) ou calendar.addEntry(entry) dependendo da versão
            
            // Exemplo de como seria a implementação:
            /*
            for (Appointment appointment : allAppointments) {
                Entry entry = new Entry();
                
                // Definir título baseado na perspectiva
                String title;
                if (currentDoctor != null && appointment.getDoctor().equals(currentDoctor)) {
                    // Esta pessoa é o médico deste agendamento
                    title = "👨‍⚕️ " + (appointment.getTitle() != null ? appointment.getTitle() : "Consulta");
                } else {
                    // Esta pessoa é o paciente deste agendamento
                    title = "🩺 Consulta com Dr. " + appointment.getDoctor().getPerson().getFirstName();
                }
                entry.setTitle(title);
                
                // Converter LocalDateTime para Instant
                Instant startInstant = appointment.getAppointmentDate().atZone(java.time.ZoneId.systemDefault()).toInstant();
                Instant endInstant = appointment.getEndDate().atZone(java.time.ZoneId.systemDefault()).toInstant();
                
                entry.setStart(startInstant);
                entry.setEnd(endInstant);
                
                // Adicionar informações customizadas
                entry.setCustomProperty("appointmentId", appointment.getId().toString());
                entry.setCustomProperty("isDoctor", 
                    currentDoctor != null && appointment.getDoctor().equals(currentDoctor) ? "true" : "false");
                
                // Cor baseada no status e perspectiva
                String color = getColorByStatus(appointment.getStatus());
                entry.setColor(color);
                
                calendar.add(entry); // ou calendar.addEntry(entry)
            }
            */
        } catch (Exception e) {
            e.printStackTrace();
            showErrorNotification("Erro ao carregar agendamentos: " + e.getMessage());
        }
    }

    private String getColorByStatus(Appointment.AppointmentStatus status) {
        switch (status) {
            case SCHEDULED: return "#2196F3"; // Azul
            case CONFIRMED: return "#4CAF50"; // Verde
            case IN_PROGRESS: return "#FF9800"; // Laranja
            case COMPLETED: return "#9E9E9E"; // Cinza
            case CANCELLED: return "#F44336"; // Vermelho
            case NO_SHOW: return "#795548"; // Marrom
            default: return "#2196F3";
        }
    }

    private void openNewAppointmentDialog() {
        openNewAppointmentDialog(null);
    }

    private void openNewAppointmentDialog(LocalDate selectedDate) {
        if (currentDoctor == null) {
            showErrorNotification("Erro: Médico não identificado");
            return;
        }

        // Por enquanto, vamos criar um agendamento de exemplo
        // Em uma implementação completa, abriria um dialog de criação
        try {
            LocalDateTime startTime = (selectedDate != null ? selectedDate : LocalDate.now()).atTime(9, 0);
            LocalDateTime endTime = startTime.plusHours(1);

            Appointment newAppointment = new Appointment();
            newAppointment.setTitle("Nova Consulta");
            newAppointment.setAppointmentDate(startTime);
            newAppointment.setEndDate(endTime);
            newAppointment.setDoctor(currentDoctor);
            // Se tem currentPerson, usa ela como paciente, senão usa nome externo
            if (currentPerson != null) {
                newAppointment.setPerson(currentPerson);
            } else {
                newAppointment.setExternalPatientName("Pessoa de Exemplo");
            }
            newAppointment.setStatus(Appointment.AppointmentStatus.SCHEDULED);

            appointmentService.validateAppointment(newAppointment);
            appointmentService.save(newAppointment);

            loadAppointments(); // Recarregar o calendário
            showSuccessNotification("Agendamento criado com sucesso!");
        } catch (Exception e) {
            showErrorNotification("Erro ao criar agendamento: " + e.getMessage());
        }
    }

    private void openEditAppointmentDialog(Integer appointmentId) {
        try {
            Optional<Appointment> appointmentOpt = appointmentService.findById(appointmentId);
            if (appointmentOpt.isPresent()) {
                Appointment appointment = appointmentOpt.get();
                
                // Por enquanto, apenas mostra informações
                // Em uma implementação completa, abriria um dialog de edição
                String message = String.format("Agendamento: %s\nPaciente: %s\nStatus: %s", 
                    appointment.getTitle(),
                    appointment.getDisplayPatientName(),
                    appointment.getStatus().getDisplayName());
                
                Notification.show(message, 3000, Notification.Position.MIDDLE);
            }
        } catch (Exception e) {
            showErrorNotification("Erro ao abrir agendamento: " + e.getMessage());
        }
    }

    private void showSuccessNotification(String message) {
        Notification notification = Notification.show(message, 3000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void showErrorNotification(String message) {
        Notification notification = Notification.show(message, 5000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
} 