#  ChupinVet API

API REST desenvolvida em Java com Spring Boot para gerenciamento de pets, responsáveis e veterinários.

O projeto foi desenvolvido para o Challenge da disciplina de Java Advanced, utilizando conceitos de Programação Orientada a Objetos (POO), Spring Boot, JPA/Hibernate e Oracle Database.

---

#  Objetivo do Projeto

A aplicação tem como objetivo auxiliar no gerenciamento de informações relacionadas ao ambiente veterinário, permitindo:

- Cadastro de responsáveis
- Cadastro de veterinários
- Cadastro de pets
- Associação de pets aos responsáveis
- Busca e gerenciamento de dados
- Consultas paginadas e filtradas

---

# Integrantes

| Nome | RM | Turma |
|---|---|---|       
|Agatha Yie Won Yun|RM561507|2TDSA| 
|Ana Claudia Fernandes Martins| RM561190| 2TDSR|  
|Anabelle Rosseto Rodrigues| RM564526| 2TDSR|  
|Samantha Faruolo Galdi| RM554794| 2TDSA|
|Vitor Fria Dalmagro| RM566052| 2TDSA|



---

# Tecnologias Utilizadas

- Java 21
- Spring Boot 4.0.6
- Spring Data JPA
- Hibernate
- Oracle Database
- Maven
- Swagger / OpenAPI
- Lombok
- Bean Validation

---

# Arquitetura do Projeto

O projeto foi estruturado seguindo o padrão de arquitetura em camadas:

```txt
Controller → Service → Repository → Database
```

## Explicando cada Camada

### Controller
Responsável pelos endpoints da aplicação.

### Service
Responsável pelas regras de negócio da aplicação.

### Repository
Responsável pela comunicação com o banco de dados utilizando Spring Data JPA.

### DTO
Responsável pela transferência de dados entre cliente e servidor.

### Exception
Responsável pelo tratamento de exceções da aplicação.

---

#  Estrutura do Projeto

```txt
src/main/java/br/com/chupinvet/chupinvet
│
├── controller
├── dto
├── exception
├── model
├── repository
└── service
```

---

# Banco de Dados

O projeto utiliza Oracle Database.

## Principais entidades

- Usuario
- Responsavel
- Veterinario
- Pet

## Relacionamentos

- Um responsável pode possuir vários pets
- Um pet pertence a um responsável
- Responsável e Veterinário herdam de Usuario

---

#  Funcionalidades Implementadas

- CRUD completo de Pets  
- CRUD completo de Responsáveis  
- CRUD completo de Veterinários  
- Paginação de resultados  
- Ordenação  
- Busca por parâmetros  
- Bean Validation  
- Tratamento de exceções  
- DTO Pattern  
- Swagger/OpenAPI  
- Relacionamentos JPA  
- Herança com JOINED

---

# Como Executar o Projeto

## 1️ Clonar o repositório

```bash
git clone https://github.com/seuusuario/chupinvet.git
```

---

## 2️ Entrar na pasta do projeto

```bash
cd chupinvet
```


---

## 3 Executar o projeto

```bash
./mvnw spring-boot:run
```

ou:

```bash
mvn spring-boot:run
```

---
# URL Base!
```txt
PLACEHOLDER
```
---
#  Swagger

Após iniciar a aplicação:

## Swagger UI

```txt
http://localhost:8080/swagger-ui.html
```

---

# Endpoints da API

## Pets

| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/pets` | Lista todos os pets |
| GET | `/pets/{id}` | Busca pet por ID |
| POST | `/pets` | Cadastra um novo pet |
| PUT | `/pets/{id}` | Atualiza um pet |
| DELETE | `/pets/{id}` | Remove um pet |
| GET | `/pets/nome?nomePet=Thor` | Busca pets por nome |
| GET | `/pets/especie?especie=Cachorro` | Busca pets por espécie |
| GET | `/pets/raca?raca=Pug` | Busca pets por raça |

---

## Responsáveis

| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/responsaveis` | Lista todos os responsáveis |
| GET | `/responsaveis/{id}` | Busca responsável por ID |
| POST | `/responsaveis` | Cadastra um responsável |
| PUT | `/responsaveis/{id}` | Atualiza um responsável |
| DELETE | `/responsaveis/{id}` | Remove um responsável |

---

## Veterinários

| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/veterinarios` | Lista todos os veterinários |
| GET | `/veterinarios/{id}` | Busca veterinário por ID |
| POST | `/veterinarios` | Cadastra um veterinário |
| PUT | `/veterinarios/{id}` | Atualiza um veterinário |
| DELETE | `/veterinarios/{id}` | Remove um veterinário |
| GET | `/veterinarios/especialidade?especialidade=Cardiologia` | Busca por especialidade |
| GET | `/veterinarios/servico?tipoServico=Consulta` | Busca por tipo de serviço |

---

# Exemplos de Busca

## Buscar pets por nome

```txt
GET /pets/nome?nomePet=Thor
```

## Buscar veterinários por especialidade

```txt
GET /veterinarios/especialidade?especialidade=Cardiologia
```

---

#  Documentação da API

A documentação completa da API pode ser acessada através do Swagger UI.

---


#  Disciplina

Java Advanced — 1º e 2° Sprint Challenge

FIAP
