package org.dasher.speed.taskmanagement.service;

import org.dasher.speed.taskmanagement.domain.Person;
import org.dasher.speed.taskmanagement.domain.User;
import org.dasher.speed.taskmanagement.repository.PersonRepository;
import org.dasher.speed.taskmanagement.security.SecurityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final SecurityService securityService;

    public PersonService(PersonRepository personRepository, SecurityService securityService) {
        this.personRepository = personRepository;
        this.securityService = securityService;
    }

    @Transactional
    public Person save(Person person) {
        return personRepository.save(person);
    }

    @Transactional(readOnly = true)
    public Optional<Person> findByUser(User user) {
        return personRepository.findByUser(user);
    }

    @Transactional(readOnly = true)
    public Optional<Person> findById(Integer id) {
        return personRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Person> findAllDoctors() {
        return personRepository.findAllDoctors();
    }
    
    @Transactional(readOnly = true)
    public List<Person> findAll() {
        return personRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Person> searchDoctorsByName(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return findAllDoctors();
        }
        return personRepository.searchDoctors(searchTerm);
    }

    @Transactional(readOnly = true)
    public List<Person> findAllPatients() {
        return personRepository.findAllPatients();
    }

    @Transactional(readOnly = true)
    public List<Person> searchPatientsByName(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return findAllPatients();
        }
        return personRepository.searchPatients(searchTerm);
    }
    
    @Transactional(readOnly = true)
    public Person getCurrentPerson() {
        User currentUser = securityService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new IllegalStateException("Nenhum usuário autenticado encontrado");
        }
        
        return findByUser(currentUser)
            .orElseThrow(() -> new IllegalStateException("Usuário atual não possui Person associada"));
    }
} 