# 📚 LifePlus Project Documentation

> **Sistema de Gestão Médica com Agenda Integrada**  
> Versão: 1.0-SNAPSHOT  
> Framework: Vaadin Flow 24.7.4 + Spring Boot 3.4.5

---

## 🗂️ **Índice da Documentação**

### 🏗️ **Arquitetura & Design**
- [📋 Visão Geral da Arquitetura](architecture/overview.md)
- [🎯 Arquitetura Backend (Java)](architecture/backend.md)
- [🌐 Arquitetura Frontend (Vaadin)](architecture/frontend.md)
- [🗄️ Modelo de Dados](architecture/data-model.md)
- [🔒 Arquitetura de Segurança](architecture/security.md)

### 🚀 **Desenvolvimento**
- [⚡ Quick Start Guide](development/quick-start.md)
- [🛠️ Configuração do Ambiente](development/environment-setup.md)
- [📝 Padrões de Código](development/coding-standards.md)
- [🧪 Testes e Qualidade](development/testing.md)
- [🔧 Debugging Guide](development/debugging.md)

### 📊 **Features & Funcionalidades**
- [👥 Gestão de Usuários](features/user-management.md)
- [📅 Sistema de Agenda Médica](features/calendar-system.md)
- [🏥 Gestão de Médicos](features/doctor-management.md)
- [🩺 Gestão de Pacientes](features/patient-management.md)
- [📋 Sistema de Agendamentos](features/appointment-system.md)

### 🔌 **API & Integração**
- [📡 API Reference](api/endpoints.md)
- [🔐 Autenticação & Autorização](api/authentication.md)
- [📦 Models & DTOs](api/models.md)
- [🧪 Testando APIs](api/testing.md)

### 🚀 **Deploy & Produção**
- [📦 Build & Package](deployment/build.md)
- [🐳 Docker Setup](deployment/docker.md)
- [☁️ Cloud Deploy](deployment/cloud.md)
- [📊 Monitoramento](deployment/monitoring.md)

### ⚙️ **Documentação Técnica**
- [📋 Dependências do Projeto](technical/dependencies.md)
- [🌐 Frontend Architecture (Vaadin)](technical/vaadin-frontend.md)
- [📦 Package.json Explained](technical/npm-dependencies.md)
- [🗃️ Database Schema](technical/database.md)
- [🔧 Configurações](technical/configurations.md)

---

## 🏁 **Quick Links**

| Categoria | Descrição | Link Direto |
|-----------|-----------|-------------|
| 🚀 **Start Here** | Para desenvolvedores novos no projeto | [Quick Start](development/quick-start.md) |
| 🏗️ **Architecture** | Entenda como o sistema funciona | [Visão Geral](architecture/overview.md) |
| 📅 **Calendar** | Sistema de agenda implementado | [Agenda Médica](features/calendar-system.md) |
| 🔧 **Setup** | Configurar ambiente de desenvolvimento | [Environment Setup](development/environment-setup.md) |
| 📡 **API** | Documentação das APIs | [API Reference](api/endpoints.md) |

---

## 📋 **Changelog Recente**

### 🆕 **v1.0-SNAPSHOT** - Atual
- ✅ **NEW:** Sistema de agenda médica com FullCalendar
- ✅ **NEW:** Gestão de agendamentos (CRUD completo)
- ✅ **NEW:** Integração Vaadin + FullCalendar
- ✅ **NEW:** Modelo de dados para appointments
- ✅ **NEW:** Segurança por roles (Doctor, Patient, Admin)

---

## 🤝 **Como Contribuir**

1. **📖 Leia a documentação** relevante antes de começar
2. **🔧 Configure o ambiente** seguindo o [Environment Setup](development/environment-setup.md)
3. **📝 Siga os padrões** descritos em [Coding Standards](development/coding-standards.md)
4. **🧪 Execute os testes** antes de submeter alterações
5. **📚 Atualize a documentação** quando necessário

---

## 🛠️ **Tecnologias Utilizadas**

| Tecnologia | Versão | Uso |
|------------|--------|-----|
| **Java** | 21 | Backend principal |
| **Spring Boot** | 3.4.5 | Framework backend |
| **Vaadin Flow** | 24.7.4 | Framework frontend |
| **FullCalendar** | 6.2.2 | Componente de agenda |
| **H2 Database** | Runtime | Banco de dados (dev) |
| **Maven** | 3.9.9 | Build tool |
| **TypeScript** | 5.7.3 | Frontend type safety |

---

## 📞 **Suporte**

- **🐛 Bugs:** Crie uma issue no repositório
- **💡 Features:** Proponha melhorias via issue
- **❓ Dúvidas:** Consulte a documentação ou crie uma discussion

---

**Última atualização:** Junho 2025  
**Mantido por:** Equipe LifePlus Development 