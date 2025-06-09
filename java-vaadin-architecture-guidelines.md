# 🧠 Java + Vaadin Architecture Guidelines for Cursor

Este documento serve como referência para a IA do Cursor ao gerar código Java com o framework Vaadin. **Todos os componentes, views e formulários devem seguir estas diretrizes.**

---

## 🎯 Objetivo

Criar código Java com Vaadin conforme arquitetura limpa, boas práticas de separação de camadas e uso correto de validação com `BeanValidationBinder`.

---

## 📁 Estrutura de Pacotes

- `com.empresa.app.application` — serviços de aplicação (`ProdutoService`)
- `com.empresa.app.domain` — entidades, enums e value objects (`Produto`)
- `com.empresa.app.infrastructure` — repositórios e integrações
- `com.empresa.app.ui` — views, formulários e componentes Vaadin

---

## 📄 Views (`*View.java`)

- Usar `@Route("rota")` e `@PageTitle("título")`
- Herdar de `VerticalLayout` ou `AppLayout`
- Injetar serviços no construtor
- Nunca incluir lógica de negócio
- Utilizar componentes reutilizáveis (ex: `ProdutoForm`)

```java
@Route("produtos")
@PageTitle("Produtos")
public class ProdutoView extends VerticalLayout {

    private final ProdutoService produtoService;

    public ProdutoView(ProdutoService produtoService) {
        this.produtoService = produtoService;
        // Adicionar componentes e lógica de layout
    }
}

