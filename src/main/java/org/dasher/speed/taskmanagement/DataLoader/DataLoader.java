package org.dasher.speed.taskmanagement.DataLoader;

import java.time.Instant;
import java.time.LocalDate;

import org.dasher.speed.taskmanagement.domain.Task;
import org.dasher.speed.taskmanagement.domain.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev") 
public class DataLoader implements CommandLineRunner {

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public void run(String... args) throws Exception {
        Instant creationDate = Instant.now(); // instante atual
        LocalDate dueDate = LocalDate.now().plusDays(7);
        var contact1 = new Task("Learn about vaadin", creationDate, dueDate);
        var contact2 = new Task("Learn more about vaadin", creationDate,dueDate);

        // Inserindo dados automaticamente
        taskRepository.save(contact1);
        taskRepository.save(contact2);
        
    }
}
    /* 
# Banco H2 em memória
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# Configuração do JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Console do H2
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
*/