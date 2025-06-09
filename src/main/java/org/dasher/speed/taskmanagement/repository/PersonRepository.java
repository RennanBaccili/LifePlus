package org.dasher.speed.taskmanagement.repository;

import org.dasher.speed.taskmanagement.domain.Person;
import org.dasher.speed.taskmanagement.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {
    Optional<Person> findByUser(User user);
} 