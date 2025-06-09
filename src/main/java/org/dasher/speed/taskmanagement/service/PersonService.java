package org.dasher.speed.taskmanagement.service;

import org.dasher.speed.taskmanagement.domain.Person;
import org.dasher.speed.taskmanagement.domain.User;
import org.dasher.speed.taskmanagement.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PersonService {

    private final PersonRepository personRepository;

    @Autowired
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
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
} 