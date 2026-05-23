#  Descrição do projeto

O ChupinVet é uma plataforma desenvolvida para melhorar a experiência entre responsáveis de animais e profissionais veterinários, oferecendo um ambiente centralizado para organização das informações relacionadas à saúde dos pets.

O projeto surgiu a partir da percepção de dificuldades comuns no mercado veterinário, como a falta de acompanhamento preventivo dos animais, dificuldades na organização das consultas e ausência de um local único para gerenciamento das informações clínicas.

A proposta da plataforma é facilitar a comunicação entre responsáveis e veterinários, permitindo o gerenciamento de pets, consultas e informações importantes de forma mais prática.

Além disso, o projeto busca incentivar o cuidado preventivo da saúde animal e proporcionar uma experiência mais eficiente tanto para os responsáveis quanto para os profissionais veterinários.

Como diferencial futuro, a plataforma prevê integração com Inteligência Artificial para geração de resumos inteligentes das consultas veterinárias e auxílio no entendimento das orientações passadas durante os atendimentos.

---

# Integrantes

| Nome | RM | Turma |
|---|---|---|       
|Agatha Yie Won Yun|RM561507|2TDSA| 
|Ana Claudia Fernandes Martins| RM561190| 2TDSR|  
|Samantha Faruolo Galdi| RM554794| 2TDSA|
|Vitor Fria Dalmagro| RM566052| 2TDSA|



---

# Benefícios para o negócio
A API ChupinVet atua como camada de comunicação entre o aplicativo mobile do projeto e o banco de dados Oracle, 
permitindo que todas as informações cadastradas no sistema sejam acessadas, 
atualizadas e gerenciadas.

Através da API, o aplicativo mobile consegue realizar operações de cadastro, 
consulta, atualização e remoção de dados relacionados a pets, responsáveis e veterinários, 
garantindo integração entre o front-end mobile e a base de dados da aplicação.

A utilização da API no projeto estão a 
centralização das regras de negócio da aplicação, permitindo que todas as 
operações e validações sejam realizadas de forma padronizada.


---

# Desenho da arquitetura
![Diagrama da Arquitetura](/IMAGES/diagrama.png)
---

# Endpoints da API

## Responsáveis

| Método | Endpoint                                             | Descrição                       |
|---|------------------------------------------------------|---------------------------------|
| POST | `/responsaveis`                                      | Cadastra um novo responsável    |
| GET | `/responsaveis`                                      | Lista todos os responsáveis     |
| GET | `/responsaveis/{id}`                                 | Busca responsável por ID        |
| GET | `/responsaveis?page=0&size=10&sort=nomeUsuario,asc ` | Busca responsável com paginação |
| PUT | `/responsaveis/{id}`                                 | Atualiza um responsável         |
| DELETE | `/responsaveis/{id}`                                 | Remove um responsável           |

---

## Pets

| Método | Endpoint | Descrição             |
|---|---|-----------------------|
| POST | `/pets` | Cadastra um novo pet  |
| GET | `/pets` | Lista todos os pets   |
| GET | `/pets/{id}` | Busca pet por ID      |
| GET | `/pets?page=0&size=10&sort=nomePet,asc` | Busca com paginação   |
| GET | `/pets/nome?nomePet=Thor` | Busca pets por nome   |
| GET | `/pets/especie?especie=Cachorro` | Busca pets por espécie |
| GET | `/pets/raca?raca=Pug` | Busca pets por raça   |
| PUT | `/pets/{id}` | Atualiza um pet       |
| DELETE | `/pets/{id}` | Remove um pet         |

---

## Veterinários

| Método | Endpoint | Descrição                              |
|---|---|----------------------------------------|
| POST | `/veterinarios` | Cadastra um novo veterinário           |
| GET | `/veterinarios` | Lista todos os veterinários            |
| GET | `/veterinarios/{id}` | Busca veterinário por ID               |
| GET | `/veterinarios?page=0&size=10&sort=nomeUsuario,asc` | Busca veterinário com paginação        |
| GET | `/veterinarios/especialidade?especialidade=Cirurgia` | Busca veterinários por especialidade   |
| GET | `/veterinarios/servico?tipoServico=Consulta` | Busca veterinários por tipo de serviço |
| PUT | `/veterinarios/{id}` | Atualiza um veterinário                |
| DELETE | `/veterinarios/{id}` | Remove um veterinário                  |

---

## Swagger

| Recurso | Endpoint |
|---|---|
| Swagger UI | `/swagger-ui.html` |

---
# Dockerfile

O projeto utiliza um Dockerfile multi-stage com otimização utilizando jlink e imagens Distroless, reduzindo significativamente o tamanho da imagem final da aplicação.

Principais características:

- Multi-stage build
- Runtime customizado com jlink
- Imagem Distroless
- Execução sem usuário root
- Otimização do tamanho da imagem Docker

---

# Docker Compose

O Docker Compose é responsável por orquestrar os containers da aplicação e do banco Oracle.

Containers utilizados:

- API Spring Boot
- Oracle Database XE

Recursos implementados:

- Volume nomeado para persistência
- Healthcheck do banco Oracle
- Comunicação interna entre containers
- Variáveis de ambiente para conexão com banco

---

# Azure CLI

O provisionamento da infraestrutura foi automatizado utilizando Azure CLI através de script Bash.

O script realiza automaticamente:

- Criação do Resource Group
- Criação da Máquina Virtual Linux
- Abertura da porta 8080
- Instalação do Docker
- Instalação do Git e Nano
- Clone do repositório
- Execução do Docker Compose
- Deploy automatizado da aplicação

---

# Como Executar Localmente

## Clonar o repositório

```bash
git clone https://github.com/ChupinVet/DevOpsChallenge.git
```

## Entrar no diretório

```bash
cd DevOpsChallenge
```

## Subir os containers

```bash
docker compose up -d
```

## Acessar a aplicação

```text
http://localhost:8080
```

## Swagger

```text
http://localhost:8080/swagger-ui.html
```

---

# Como Executar na Azure
### Lembre-se de alterar os valores de LOCATION e SIZE no script de acordo com disponibilidade, preferências e políticas da sua conta AZURE!

## Clonar o repositório

```bash
git clone https://github.com/ChupinVet/DevOpsChallenge.git
```

## Entrar no diretório

```bash
cd DevOpsChallenge
```

## Realizar login na Azure CLI

```bash
az login
```

## Executar script de provisionamento
```bash
./azure/criar-infra.sh
```

O script irá:

- Provisionar a infraestrutura na Azure
- Instalar Docker automaticamente
- Clonar o repositório
- Executar os containers
- Disponibilizar a API publicamente

---

#  Disciplina

DevOps Tools e Cloud Computing — 1º e 2° Sprint Challenge

FIAP
