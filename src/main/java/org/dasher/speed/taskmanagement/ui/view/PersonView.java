package org.dasher.speed.taskmanagement.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.dasher.speed.base.ui.component.ViewToolbar;
import org.dasher.speed.taskmanagement.domain.Person;
import org.dasher.speed.taskmanagement.domain.User;
import org.dasher.speed.taskmanagement.domain.Doctor;
import org.dasher.speed.taskmanagement.domain.Patient;
import org.dasher.speed.taskmanagement.domain.Enums.PersonRole;
import org.dasher.speed.taskmanagement.service.PersonService;
import org.dasher.speed.taskmanagement.service.UserService;
import com.vaadin.flow.component.select.Select;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.core.context.SecurityContextHolder;

@Route("person")
@PageTitle("View Profile | LifePlus")
@RolesAllowed({"USER", "ADMIN"})
public class PersonView extends VerticalLayout {
    private final PersonService personService;
    private final UserService userService;

    private final BeanValidationBinder<Person> personBinder = new BeanValidationBinder<>(Person.class);
    private final BeanValidationBinder<User> userBinder = new BeanValidationBinder<>(User.class);

    private Person currentPerson;
    private User currentUser;

    // Campos do formulário
    private final EmailField email = new EmailField("Email");
    private final PasswordField password = new PasswordField("Nova Senha");
    private final PasswordField confirmPassword = new PasswordField("Confirmar Nova Senha");
    private final TextField firstName = new TextField("Nome");
    private final TextField lastName = new TextField("Sobrenome");
    private final TextField phone = new TextField("Telefone");
    private final TextField cpf = new TextField("CPF");
    private final Select<String> gender = new Select<>();
    private final Select<String> maritalStatus = new Select<>();
    private final Select<PersonRole> role = new Select<>();

    // Campos específicos do Doctor
    private final TextField licenseNumber = new TextField("Número da Licença");
    private final TextField licenseState = new TextField("Estado da Licença");
    private final TextField medicalSpecialty = new TextField("Especialidade Médica");
    private final TextField digitalSignature = new TextField("Assinatura Digital");

    // Campos específicos do Patient
    private final TextField healthPlan = new TextField("Plano de Saúde");

    private final Button saveButton = new Button("Salvar");
    private final Button cancelButton = new Button("Cancelar");

    private final FormLayout formLayout = new FormLayout();

    public PersonView(PersonService personService, UserService userService) {
        this.personService = personService;
        this.userService = userService;

        setupLayout();
        configureFields();
        configureBinders();
        loadData();
    }

    private void setupLayout() {
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        var toolbar = new ViewToolbar("Dados Pessoais");
        add(toolbar);

        H2 title = new H2("Gerenciar Dados Pessoais");
        formLayout.add(title);
    }

    private void configureFields() {
        // Configurar Select de Gênero
        gender.setLabel("Gênero");
        gender.setItems("Masculino", "Feminino", "Outro");
        gender.setPlaceholder("Selecione...");

        // Configurar Select de Estado Civil
        maritalStatus.setLabel("Estado Civil");
        maritalStatus.setItems("Solteiro(a)", "Casado(a)", "Divorciado(a)", "Viúvo(a)");
        maritalStatus.setPlaceholder("Selecione...");

        // Configurar Select de Role
        role.setLabel("Tipo de Usuário");
        role.setItems(PersonRole.PATIENT, PersonRole.DOCTOR);
        role.setItemLabelGenerator(this::getRoleLabel);
        role.setPlaceholder("Selecione...");

        // Configurar campo de senha
        password.setHelperText("Deixe vazio para não alterar");
        confirmPassword.setHelperText("Repita a nova senha");

        // Configurar campos condicionais (inicialmente ocultos)
        setRoleSpecificFieldsVisible(false);

        // Listener para mostrar/esconder campos específicos
        role.addValueChangeListener(event -> {
            PersonRole selectedRole = event.getValue();
            setRoleSpecificFieldsVisible(selectedRole != null);
            updateFieldVisibility(selectedRole);
        });

        // Criar FormLayout principal
        FormLayout formLayout = this.formLayout;
        formLayout.setMaxWidth("600px");
        formLayout.getStyle().set("margin", "0 auto");
        formLayout.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0", 1),
            new FormLayout.ResponsiveStep("500px", 2)
        );

        // Adicionar campos ao formulário
        formLayout.add(email);
        formLayout.setColspan(email, 2);
        formLayout.add(password, confirmPassword);
        formLayout.add(firstName, lastName);
        formLayout.add(phone, cpf);
        formLayout.add(gender, maritalStatus);
        formLayout.add(role);
        formLayout.setColspan(role, 2);
        // Adicionar seções específicas
        formLayout.add(createDoctorSection());
        formLayout.add(createPatientSection());
        configureButtons();
        add(formLayout);


    }

    private String getRoleLabel(PersonRole role) {
        switch (role) {
            case PATIENT: return "Paciente";
            case DOCTOR: return "Médico";
            case ADMIN: return "Administrador";
            default: return role.name();
        }
    }

    private VerticalLayout createDoctorSection() {
        VerticalLayout doctorSection = new VerticalLayout();
        doctorSection.setMaxWidth("600px");
        doctorSection.setPadding(false);
        doctorSection.setSpacing(true);
        doctorSection.addClassName("doctor-section");

        com.vaadin.flow.component.html.H3 doctorTitle = new com.vaadin.flow.component.html.H3("Dados do Médico");
        
        FormLayout doctorForm = new FormLayout();
        doctorForm.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0", 1),
            new FormLayout.ResponsiveStep("500px", 2)
        );
        
        doctorForm.add(licenseNumber, licenseState);
        doctorForm.add(medicalSpecialty, digitalSignature);

        doctorSection.add(doctorTitle, doctorForm);
        return doctorSection;
    }

    private VerticalLayout createPatientSection() {
        VerticalLayout patientSection = new VerticalLayout();
        patientSection.setMaxWidth("600px");
        patientSection.setPadding(false);
        patientSection.setSpacing(true);
        patientSection.addClassName("patient-section");

        com.vaadin.flow.component.html.H3 patientTitle = new com.vaadin.flow.component.html.H3("Dados do Paciente");
        
        FormLayout patientForm = new FormLayout();
        patientForm.add(healthPlan);

        patientSection.add(patientTitle, patientForm);
        return patientSection;
    }

    private void setRoleSpecificFieldsVisible(boolean visible) {
        // Buscar as seções e definir visibilidade
        formLayout.getChildren()
            .filter(component -> component.getClass().equals(VerticalLayout.class))
            .map(component -> (VerticalLayout) component)
            .filter(layout -> layout.getClassName() != null && 
                    (layout.getClassName().contains("doctor-section") || 
                     layout.getClassName().contains("patient-section")))
            .forEach(layout -> layout.setVisible(visible));
    }

    private void updateFieldVisibility(PersonRole selectedRole) {
        boolean showDoctor = selectedRole == PersonRole.DOCTOR;
        boolean showPatient = selectedRole == PersonRole.PATIENT;

        formLayout.getChildren()
            .filter(component -> component.getClass().equals(VerticalLayout.class))
            .map(component -> (VerticalLayout) component)
            .forEach(layout -> {
                if (layout.getClassName() != null) {
                    if (layout.getClassName().contains("doctor-section")) {
                        layout.setVisible(showDoctor);
                    } else if (layout.getClassName().contains("patient-section")) {
                        layout.setVisible(showPatient);
                    }
                }
            });
    }

    private void configureBinders() {
        // Configurar binder do User - SEM validação bean automática para senha
        userBinder.forField(email)
            .asRequired("Email é obrigatório")
            .bind("email");

        // Configurar binder do Person - COM validação bean automática
        personBinder.bindInstanceFields(this);
        
        // Sobrescrever bindings específicos se necessário
        personBinder.forField(firstName)
            .asRequired("Nome é obrigatório")
            .bind("firstName");
            
        personBinder.forField(lastName)
            .asRequired("Sobrenome é obrigatório")
            .bind("lastName");

        personBinder.forField(role)
            .asRequired("Tipo de usuário é obrigatório")
            .bind("role");
    }

    private void configureButtons() {
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> save());

        cancelButton.addClickListener(e -> loadData());

        HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton, saveButton);
        buttonLayout.setMaxWidth("600px");
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);
        formLayout.add(buttonLayout);
        formLayout.setColspan(buttonLayout, 2);
    }

    private void loadData() {
        try {
            // Obter usuário autenticado
            String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
            currentUser = (User) userService.loadUserByUsername(currentUsername);
            
            if (currentUser == null) {
                showErrorNotification("Usuário não encontrado");
                return;
            }

            // Carregar dados do usuário no binder
            email.setValue(currentUser.getEmail());

            // Buscar ou criar Person
            currentPerson = personService.findByUser(currentUser)
                .orElseGet(() -> {
                    Person newPerson = new Person();
                    newPerson.setUser(currentUser);
                    return newPerson;
                });

            // Carregar dados do person no binder
            personBinder.readBean(currentPerson);

            // Carregar dados específicos do role (Doctor/Patient)
            loadRoleSpecificData();

            // Limpar campos de senha
            password.clear();
            confirmPassword.clear();

        } catch (Exception e) {
            e.printStackTrace();
            showErrorNotification("Erro ao carregar dados: " + e.getMessage());
        }
    }

    private void save() {
        try {
            // Validar se as senhas coincidem (se preenchidas)
            if (!password.getValue().isEmpty()) {
                if (!password.getValue().equals(confirmPassword.getValue())) {
                    showErrorNotification("As senhas não coincidem");
                    return;
                }
                if (password.getValue().length() < 8) {
                    showErrorNotification("A senha deve ter pelo menos 8 caracteres");
                    return;
                }
            }

            // Validar e salvar dados do usuário
            userBinder.writeBean(currentUser);
            
            // Atualizar senha se necessário
            if (!password.getValue().isEmpty()) {
                currentUser.setPassword(password.getValue());
            }

            User savedUser = userService.update(currentUser);
            System.out.println("Usuário salvo: " + savedUser.getEmail());

            // Validar e salvar dados da pessoa
            personBinder.writeBean(currentPerson);
            
            // Criar/atualizar Doctor ou Patient baseado no role
            handleRoleSpecificData(currentPerson);
            
            Person savedPerson = personService.save(currentPerson);
            System.out.println("Person salvo: " + savedPerson.getFirstName() + " - Role: " + savedPerson.getRole());

            // Limpar campos de senha
            password.clear();
            confirmPassword.clear();

            showSuccessNotification("Dados salvos com sucesso!");
            System.out.println("=== SALVAMENTO CONCLUÍDO ===");

        } catch (ValidationException e) {
            System.err.println("Erro de validação: " + e.getMessage());
            showErrorNotification("Erro de validação. Verifique os campos obrigatórios.");
        } catch (Exception e) {
            e.printStackTrace();
            showErrorNotification("Erro ao salvar: " + e.getMessage());
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

    private void handleRoleSpecificData(Person person) {
        PersonRole selectedRole = person.getRole();
        
        if (selectedRole == PersonRole.DOCTOR) {
            handleDoctorData(person);
            // Remover dados de patient se existir
            person.setPatient(null);
        } else if (selectedRole == PersonRole.PATIENT) {
            handlePatientData(person);
            // Remover dados de doctor se existir
            person.setDoctor(null);
        } else {
            // Se não é doctor nem patient, limpar ambos
            person.setDoctor(null);
            person.setPatient(null);
        }
    }

    private void handleDoctorData(Person person) {
        Doctor doctor = person.getDoctor();
        if (doctor == null) {
            doctor = new Doctor();
            doctor.setPerson(person);
            person.setDoctor(doctor);
        }
        
        // Atualizar dados do doctor com os campos do formulário
        doctor.setLicenseNumber(licenseNumber.getValue());
        doctor.setLicenseState(licenseState.getValue());
        doctor.setMedicalSpecialty(medicalSpecialty.getValue());
        doctor.setDigitalSignature(digitalSignature.getValue());
    }

    private void handlePatientData(Person person) {
        Patient patient = person.getPatient();
        if (patient == null) {
            patient = new Patient();
            patient.setPerson(person);
            person.setPatient(patient);
        }
        
        // Atualizar dados do patient com os campos do formulário
        patient.setHealthPlan(healthPlan.getValue());
    }

    private void loadRoleSpecificData() {
        if (currentPerson.getRole() == PersonRole.DOCTOR && currentPerson.getDoctor() != null) {
            Doctor doctor = currentPerson.getDoctor();
            licenseNumber.setValue(doctor.getLicenseNumber() != null ? doctor.getLicenseNumber() : "");
            licenseState.setValue(doctor.getLicenseState() != null ? doctor.getLicenseState() : "");
            medicalSpecialty.setValue(doctor.getMedicalSpecialty() != null ? doctor.getMedicalSpecialty() : "");
            digitalSignature.setValue(doctor.getDigitalSignature() != null ? doctor.getDigitalSignature() : "");
        } else if (currentPerson.getRole() == PersonRole.PATIENT && currentPerson.getPatient() != null) {
            Patient patient = currentPerson.getPatient();
            healthPlan.setValue(patient.getHealthPlan() != null ? patient.getHealthPlan() : "");
        }
        
        // Atualizar visibilidade dos campos
        updateFieldVisibility(currentPerson.getRole());
    }
} 