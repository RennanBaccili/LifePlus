package org.dasher.speed.taskmanagement.repository;

import org.dasher.speed.taskmanagement.domain.Person;
import org.dasher.speed.taskmanagement.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {
    Optional<Person> findByUser(User user);

    @Query("select p from Person p where p.role = org.dasher.speed.taskmanagement.domain.Enums.PersonRole.DOCTOR")
    List<Person> findAllDoctors();

    @Query("select p from Person p where p.role = org.dasher.speed.taskmanagement.domain.Enums.PersonRole.DOCTOR and " +
           "(lower(p.firstName) like lower(concat('%', :searchTerm, '%')) or " +
           "lower(p.lastName) like lower(concat('%', :searchTerm, '%')))")
    List<Person> searchDoctors(@Param("searchTerm") String searchTerm);

    @Query("select c from Person c" +
    " where lower(c.firstName) like lower(concat('%', :searchTerm, '%'))" + 
    " or lower(c.lastName) like lower(concat('%', :searchTerm, '%'))") 
    List<Person> search(@Param("searchTerm") String filterText);
} 