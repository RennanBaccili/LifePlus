# Java + Vaadin Architecture Guidelines

## 🎯 Objetivo
Este documento define a arquitetura e boas práticas a serem seguidas no desenvolvimento de aplicações Java usando o framework Vaadin.

## 🧱 Arquitetura do Projeto

### Camadas da Aplicação

1. **Repository Layer**
   - Responsável pelo acesso a dados
   - Nomenclatura: sufixo `Repository`
   - Localização: `com.seuprojeto.repository`
   - Implementa interfaces do Spring Data JPA
   - Não contém lógica de negócio

2. **Service Layer**
   - Contém toda a lógica de negócio
   - Nomenclatura: sufixo `Service`
   - Localização: `com.seuprojeto.service`
   - Orquestra operações entre diferentes repositories
   - Gerencia transações

3. **Domain Layer**
   - Contém as entidades (models) e enums
   - Localização: `com.seuprojeto.domain`
   - Implementa validações de domínio
   - Utiliza JPA/Hibernate para mapeamento

4. **View Layer (Vaadin)**
   - Interface com usuário
   - Localização: `com.seuprojeto.view`
   - Apenas apresentação e captura de dados
   - Sem lógica de negócio
   - Usa `Binder` para ligação com domain

## 📦 Estrutura de Pacotes

```
com.seuprojeto
├── domain/
│   ├── entities/
│   ├── enums/
│   └── validation/
├── repository/
├── service/
└── view/
    ├── components/
    ├── layout/
    └── views/
```

## ☕ Boas Práticas Java

### Convenções de Código
- CamelCase para classes: `PersonService`
- camelCase para métodos e variáveis: `findByEmail()`
- UPPER_SNAKE_CASE para constantes: `MAX_ATTEMPTS`

### Princípios
1. **Injeção de Dependências**
   ```java
   @Service
   public class PersonService {
       private final PersonRepository repository;
       
       public PersonService(PersonRepository repository) {
           this.repository = repository;
       }
   }
   ```

2. **Tratamento de Nulos**
   ```java
   public Optional<Person> findById(Long id) {
       return repository.findById(id);
   }
   ```

3. **Validações no Domain**
   ```java
   @Entity
   public class Person {
       public void validate() {
           if (name == null || name.trim().isEmpty()) {
               throw new ValidationException("Name is required");
           }
       }
   }
   ```

## 🖼️ Boas Práticas Vaadin

### Estrutura de Views
```java
@Route("person")
public class PersonView extends VerticalLayout {
    private final PersonService service;
    private final Binder<Person> binder;
    
    public PersonView(PersonService service) {
        this.service = service;
        this.binder = new Binder<>(Person.class);
        configureView();
    }
}
```

### Componentes Reutilizáveis
```java
public class CustomForm extends FormLayout {
    private final TextField name = new TextField("Name");
    private final EmailField email = new EmailField("Email");
    
    public CustomForm() {
        configureForm();
    }
}
```

## ⚠️ Anti-Patterns a Evitar

1. **Não usar DTOs**
   - Trabalhar diretamente com entidades do domínio
   - Usar projeções JPA quando necessário

2. **Não implementar lógica na View**
   ```java
   // ❌ Errado
   @Route("person")
   public class PersonView extends VerticalLayout {
       public void save() {
           // Lógica de negócio aqui
       }
   }
   
   // ✅ Correto
   @Route("person")
   public class PersonView extends VerticalLayout {
       public void save() {
           service.save(person);
       }
   }
   ```

3. **Não usar EntityManager diretamente**
   - Usar repositories do Spring Data JPA
   - Encapsular operações de banco em repositories

## 🔍 Validação de Código

Antes de cada commit, verificar:
1. Lógica de negócio está nos services?
2. Views estão apenas orquestrando chamadas?
3. Validações estão no domain?
4. Repositories estão focados em persistência?
5. Componentes Vaadin estão reutilizáveis?

## 📚 Exemplos de Implementação

### Domain
```java
@Entity
public class Person {
    @Id
    @GeneratedValue
    private Long id;
    
    @NotNull
    private String name;
    
    public void validate() {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Name is required");
        }
    }
}
```

### Repository
```java
@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    Optional<Person> findByEmail(String email);
}
```

### Service
```java
@Service
@Transactional
public class PersonService {
    private final PersonRepository repository;
    
    public PersonService(PersonRepository repository) {
        this.repository = repository;
    }
    
    public Person save(Person person) {
        person.validate();
        return repository.save(person);
    }
}
```

### View
```java
@Route("person")
public class PersonView extends VerticalLayout {
    private final PersonService service;
    private final Binder<Person> binder;
    private final TextField name = new TextField("Name");
    
    public PersonView(PersonService service) {
        this.service = service;
        this.binder = new Binder<>(Person.class);
        
        binder.bindInstanceFields(this);
        add(name, createSaveButton());
    }
    
    private Button createSaveButton() {
        return new Button("Save", e -> save());
    }
    
    private void save() {
        Person person = new Person();
        binder.writeBeanIfValid(person);
        service.save(person);
    }
}
``` 