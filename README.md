# 🛠️ Sistema de Gerenciamento de Chamados

## 🇧🇷 

### 💻 Sobre o Projeto

Esta é uma API RESTful desenvolvida em Java com Spring Boot para o gerenciamento de chamados. O projeto foi construído com foco em boas práticas de Engenharia de Software, separação de responsabilidades e regras de negócio rigorosas.

### 🚀 Tecnologias e Padrões Utilizados

* **Java 21+**

* **Spring Boot** (Web, Data JPA, Validation)

* **H2 Database** (Banco de dados em memória para testes)

* **Maven** (Gerenciamento de dependências)

### 🚦 Como Executar o Projeto

1. Certifique-se de ter o **JDK 21** ou superior instalado.

2. Clone o repositório.

3. No terminal, na raiz do projeto, execute o comando:

```
.\mvnw spring-boot:run

```

*A API estará disponível em `http://localhost:8080`.*

### 🧪 Guia Prático de Testes (Postman)

Como o projeto utiliza um banco de dados em memória (H2), os dados são reiniciados a cada execução. Siga o roteiro abaixo para popular o banco e testar as regras de negócio.

#### 1. Setup Inicial (POST)

**Criar Cliente:** `POST http://localhost:8080/clientes`

```
{ 
  "nome": "Mateus Tavares dos Santos", 
  "email": "mateus@gmail.com", 
  "telefone": "47999999999" 
}

```

**Criar Categoria:** `POST http://localhost:8080/categorias`

```
{
  "nome": "Atualização de conta",
  "descricao": "Atualizar informações sobre a conta do usuário/cliente"
}

```

**Criar Técnico:** `POST http://localhost:8080/tecnicos`

```
{
  "nome": "Emilly",
  "email": "emilly@gmail.com",
  "perfil": "TECNICO"
}

```

#### 2. Ciclo de Vida do Chamado

**Abrir Chamado:** `POST http://localhost:8080/chamados`

```
{
  "titulo": "Mudei de nome",
  "descricao": "Alterei meu nome e necissito atualização do nome da minha conta",
  "prioridade": "ALTA",
  "clienteId": 1,
  "categoriaId": 1
}

```

**Adicionar Comentário:** `POST http://localhost:8080/chamados/1/comentarios`

```
{ 
  "texto": "Verifiquem a fonte de energia.", 
  "autor": "Sistema" 
}

```

**Atribuir Técnico (PATCH):** `PATCH http://localhost:8080/chamados/1/atribuir`

```
{
  "tecnicoId": 1
}

```

**Fechar Chamado (PATCH):** `PATCH http://localhost:8080/chamados/1/status`

```
{
  "status": "FECHADO"
}

```

*(Dica de teste: Tente adicionar um novo comentário ou alterar o status após o chamado ser fechado para acionar o erro 409 Conflict gerado pela nossa exceção customizada!)*

#### 3. Consultas, Filtros e Paginação (GET)

A API suporta paginação e filtros dinâmicos de alta performance. Teste as seguintes rotas GET:

* **Paginação simples (tamanho 2):**
  `http://localhost:8080/chamados?size=2`

* **Paginação específica (Página 0, tamanho 2):**
  `http://localhost:8080/chamados?size=2&page=0`

* **Filtrar apenas por Status:**
  `http://localhost:8080/chamados?status=EM_ATENDIMENTO`

* **Filtros combinados (Status + Prioridade):**
  `http://localhost:8080/chamados?status=EM_ATENDIMENTO&prioridade=ALTA`

* **Buscar todos os chamados de um cliente específico:**
  `http://localhost:8080/clientes/1/chamados`

* **Detalhe completo de um chamado:**
  `http://localhost:8080/chamados/1`

* **Histórico isolado de comentários de um chamado:**
  `http://localhost:8080/chamados/1/comentarios`

## 🇬🇧

### 💻 About the Project

This is a RESTful API developed in Java with Spring Boot for Support Ticket Management. The project was built focusing on Software Engineering best practices, separation of concerns, and strict business rules validation.

### 🚀 Technologies and Practices

* **Java 21+**

* **Spring Boot** (Web, Data JPA, Validation)

* **H2 Database** (In-memory database for testing)

* **Maven** (Dependency Manager)

### 🚦 How to Run the Project

1. Ensure you have **JDK 21** or higher installed.

2. Clone the repository.

3. In the terminal, at the project root, run the command:

```
.\mvnw spring-boot:run

```

*The API will be available at `http://localhost:8080`.*

### 🧪 Practical Testing Guide (Postman)

Since the project uses an in-memory database (H2), data is wiped upon restart. Follow the script below to populate the database and test the business rules.

#### 1. Initial Setup (POST)

**Create Client:** `POST http://localhost:8080/clientes`

```
{ 
  "nome": "Mateus Tavares dos Santos", 
  "email": "mateus@gmail.com", 
  "telefone": "47999999999" 
}

```

**Create Category:** `POST http://localhost:8080/categorias`

```
{
  "nome": "Atualização de conta",
  "descricao": "Atualizar informações sobre a conta do usuário/cliente"
}

```

**Create Tech:** `POST http://localhost:8080/tecnicos`

```
{
  "nome": "Emilly",
  "email": "emilly@gmail.com",
  "perfil": "TECNICO"
}

```

#### 2. Ticket Lifecycle

**Open Ticket:** `POST http://localhost:8080/chamados`

```
{
  "titulo": "Mudei de nome",
  "descricao": "Alterei meu nome e necissito atualização do nome da minha conta",
  "prioridade": "ALTA",
  "clienteId": 1,
  "categoriaId": 1
}

```

**Add Comment:** `POST http://localhost:8080/chamados/1/comentarios`

```
{ 
  "texto": "Verifiquem a fonte de energia.", 
  "autor": "Sistema" 
}

```

**Assign Tech (PATCH):** `PATCH http://localhost:8080/chamados/1/atribuir`

```
{
  "tecnicoId": 1
}

```

**Close Ticket (PATCH):** `PATCH http://localhost:8080/chamados/1/status`

```
{
  "status": "FECHADO"
}

```

*(Testing tip: Try adding a new comment or changing the status after the ticket is closed to trigger the 409 Conflict error generated by our custom exception!)*

#### 3. Queries, Filters, and Pagination (GET)

The API supports high-performance dynamic pagination and filters. Test the following GET routes:

* **Simple Pagination (size 2):**
  `http://localhost:8080/chamados?size=2`

* **Specific Pagination (Page 0, size 2):**
  `http://localhost:8080/chamados?size=2&page=0`

* **Filter purely by Status:**
  `http://localhost:8080/chamados?status=EM_ATENDIMENTO`

* **Combined Filters (Status + Priority):**
  `http://localhost:8080/chamados?status=EM_ATENDIMENTO&prioridade=ALTA`

* **Get all tickets from a specific client:**
  `http://localhost:8080/clientes/1/chamados`

* **Full ticket detail:**
  `http://localhost:8080/chamados/1`

* **Isolated ticket comments history:**
  `http://localhost:8080/chamados/1/comentarios`