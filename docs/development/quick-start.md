# ⚡ Quick Start Guide

> **Objetivo:** Colocar o projeto LifePlus rodando em **menos de 10 minutos**

---

## 🎯 **Pré-requisitos**

Antes de começar, certifique-se de ter instalado:

| Ferramenta | Versão Mínima | Download |
|------------|---------------|----------|
| **Java JDK** | 21+ | [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) |
| **Maven** | 3.8+ | [Apache Maven](https://maven.apache.org/download.cgi) |
| **Git** | 2.0+ | [Git Downloads](https://git-scm.com/downloads) |
| **IDE** | Qualquer | [IntelliJ IDEA](https://www.jetbrains.com/idea/) (recomendado) |

### ✅ **Verificar Instalação**
```bash
java -version    # Deve mostrar Java 21+
mvn -version     # Deve mostrar Maven 3.8+
git --version    # Deve mostrar Git 2.0+
```

---

## 🚀 **Passos Rápidos**

### **1️⃣ Clone o Repositório**
```bash
git clone <repository-url>
cd LifePlusProject
```

### **2️⃣ Execute o Projeto**
```bash
# Compilar e baixar dependências
mvn clean compile

# Executar o projeto
mvn spring-boot:run
```

### **3️⃣ Acesse a Aplicação**
🌐 **URL:** http://localhost:8080

### **4️⃣ Login Inicial**
👤 **Credenciais de teste:**
- **Usuário:** `admin@lifeplus.com`
- **Senha:** `admin123`

---

## 🎯 **Testando Funcionalidades**

### **📅 Agenda Médica**
1. Faça login como **médico**
2. Acesse: http://localhost:8080/calendar
3. Clique em **"Novo Agendamento"**
4. Veja o calendário funcionando! 

### **👥 Gestão de Usuários**
1. Acesse: http://localhost:8080/person
2. Edite seus dados pessoais
3. Configure perfil como **Doctor** ou **Patient**

---

## 🛠️ **Desenvolvimento**

### **🔧 Modo Desenvolvimento**
```bash
# Executar com hot-reload
mvn spring-boot:run -Dspring-boot.run.profiles=development

# Compilação automática (em outro terminal)
mvn compile -Dvaadin.productionMode=false
```

### **📁 Estrutura Principal**
```
src/main/java/
├── domain/           # Entidades (User, Doctor, Patient, Appointment)
├── repository/       # Repositórios JPA
├── service/          # Lógica de negócio
├── ui/view/          # Views Vaadin (PersonView, CalendarView)
└── security/         # Configuração de segurança
```

### **🗄️ Banco de Dados (H2)**
- **Console:** http://localhost:8080/h2-console
- **JDBC URL:** `jdbc:h2:file:./taskmanagement`
- **Username:** `sa`
- **Password:** *(vazio)*

---

## ❗ **Troubleshooting**

### **🚫 Problema: Porta 8080 ocupada**
```bash
# Mudar porta
mvn spring-boot:run -Dserver.port=8081
```

### **🚫 Problema: Erro de compilação**
```bash
# Limpar cache
mvn clean install -U

# Se ainda der erro, deletar cache
rm -rf ~/.m2/repository
mvn install
```

### **🚫 Problema: FullCalendar não carrega**
```bash
# Recompilар frontend
mvn vaadin:clean-frontend vaadin:prepare-frontend
mvn spring-boot:run
```

### **🚫 Problema: Erro de memória**
```bash
# Aumentar memória JVM
export MAVEN_OPTS="-Xmx2048m"
mvn spring-boot:run
```

---

## 🎯 **Próximos Passos**

Agora que está funcionando, explore:

1. **📖 [Arquitetura do Sistema](../architecture/overview.md)** - Entenda como funciona
2. **🏗️ [Configuração Completa](environment-setup.md)** - Setup completo para desenvolvimento
3. **📅 [Sistema de Agenda](../features/calendar-system.md)** - Detalhes da agenda médica
4. **🔐 [Segurança](../architecture/security.md)** - Como funciona a autenticação

---

## 🆘 **Precisa de Ajuda?**

- **💬 Problemas:** Consulte [Debugging Guide](debugging.md)
- **📚 Documentação:** Volte ao [README principal](../README.md)
- **🐛 Bugs:** Reporte no repositório

---

**⏱️ Tempo estimado:** 5-10 minutos  
**✅ Resultado esperado:** Aplicação rodando em http://localhost:8080  
**🎯 Próximo:** [Environment Setup](environment-setup.md) 