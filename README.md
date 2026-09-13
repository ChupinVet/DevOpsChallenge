# Descrição do projeto

O ChupinVet é uma plataforma desenvolvida para melhorar a experiência entre responsáveis de 
animais e profissionais veterinários, oferecendo um ambiente centralizado para organização das
informações relacionadas à saúde dos pets.

O projeto surgiu a partir da percepção de dificuldades comuns no mercado veterinário, 
como a falta de acompanhamento preventivo dos animais, 
dificuldades na organização das consultas e ausência de um local único 
para gerenciamento das informações clínicas.

A proposta da plataforma é facilitar a comunicação entre responsáveis e veterinários, 
permitindo o gerenciamento de pets, consultas e informações importantes de forma mais prática.

Além disso, o projeto busca incentivar o cuidado preventivo da saúde animal e proporcionar 
uma experiência mais eficiente tanto para os responsáveis quanto para os profissionais veterinários.

Como diferencial futuro, a plataforma prevê integração com Inteligência Artificial para 
geração de resumos inteligentes das consultas veterinárias e auxílio no entendimento das 
orientações passadas durante os atendimentos.

Este repositório contém a entrega da disciplina **DevOps Tools & Cloud Computing**, 
focada em containerizar a API e o banco de dados e implantá-los na nuvem usando 
**Azure Container Registry (ACR)** e **Azure Container Instances (ACI)**.

---

# Integrantes

| Nome | RM | Turma |
|---|---|---|
| Agatha Yie Won Yun | RM561507 | 2TDSA |
| Ana Claudia Fernandes Martins | RM561190 | 2TDSR |
| Samantha Faruolo Galdi | RM554794 | 2TDSA |
| Vitor Fria Dalmagro | RM566052 | 2TDSA |

---

# Benefícios para o negócio

- **Histórico centralizado**: todo o acompanhamento de saúde do pet (diário de comportamento, alimentação, peso) 
fica num só lugar, acessível tanto pelo responsável quanto pelo veterinário e acaba com a perda de informação entre consultas.
- **Comunicação mais direta** entre responsável e veterinário, reduzindo retrabalho administrativo de cadastro e busca de informações.
- **Disponibilidade e escalabilidade na nuvem**: rodando em containers no Azure, a solução não depende de infraestrutura própria e pode ser replicada/escalada conforme a demanda.
- **Custo sob controle**: Azure Container Instances cobra por segundo de uso (Pay-as-you-go),não há custo de infraestrutura ociosa como haveria numa VM ligada o tempo todo.
- **Segurança de credenciais**: senhas e segredos de conexão ficam no Azure Key Vault, nunca no código-fonte (que é público).

---

# Desenho da arquitetura

![Diagrama da Arquitetura](/IMAGES/diagramav2.png)

## Componentes

- **Azure Container Registry (ACR)**: registro privado com as duas imagens da solução (`chupinvet/api` e `chupinvet/mysql`).
- **Container Group único no ACI**, com dois containers:
  - `api`: a aplicação Spring Boot, única porta exposta publicamente (8080).
  - `mysql`: banco MySQL 8.4 customizado, acessível **somente via `localhost` dentro do próprio grupo**, nunca exposto à internet.
- **Azure Storage Account + Azure Files**: volume persistente montado no container do MySQL, garantindo que os dados sobrevivam a reinicializações do container.
- **Azure Key Vault**: guarda a senha do banco e o segredo do JWT. Nenhum segredo fica no repositório.


---

# Banco de Dados

O banco é **MySQL 8.4** LTS lançado em abril de 2024, containerizado, com uma imagem customizada (pasta `mysql/`). 
## Tabelas

O schema tem 5 tabelas (`usuario`, `responsavel`, `veterinario`, `pet`, `diario`). As duas usadas para demonstrar o CRUD completo exigido pelo checkpoint são **`pet`** e **`diario`** (relação N:1, um pet tem vários registros de diário).

Ver `script_bd.sql` na raiz do repositório para o DDL completo.

---

# Endpoints da API

## Autenticação

| Método | Endpoint | Descrição | Acesso |
|---|---|---|---|
| POST | `/auth/login` | Login (retorna token JWT) | Público |

## Responsáveis

| Método | Endpoint | Descrição | Acesso |
|---|---|---|---|
| POST | `/responsaveis` | Cadastra um novo responsável (signup) | Público |
| GET | `/responsaveis` | Lista todos os responsáveis | Veterinário |
| GET | `/responsaveis/{id}` | Busca responsável por ID | Veterinário, ou o próprio responsável |
| GET | `/responsaveis?page=0&size=10&sort=nomeUsuario,asc` | Busca com paginação/ordenação | Veterinário |
| PUT | `/responsaveis/{id}` | Atualiza um responsável | O próprio responsável |
| DELETE | `/responsaveis/{id}` | Remove um responsável | O próprio responsável |

## Veterinários

| Método | Endpoint | Descrição | Acesso |
|---|---|---|---|
| POST | `/veterinarios` | Cadastra um novo veterinário (signup) | Público |
| GET | `/veterinarios` | Lista todos os veterinários | Autenticado |
| GET | `/veterinarios/{id}` | Busca veterinário por ID | Autenticado |
| GET | `/veterinarios?page=0&size=10&sort=nomeUsuario,asc` | Busca com paginação/ordenação | Autenticado |
| GET | `/veterinarios/especialidade?especialidade=Cirurgia` | Busca por especialidade | Autenticado |
| GET | `/veterinarios/servico?tipoServico=Consulta` | Busca por tipo de serviço | Autenticado |
| PUT | `/veterinarios/{id}` | Atualiza um veterinário | O próprio veterinário |
| DELETE | `/veterinarios/{id}` | Remove um veterinário | O próprio veterinário |

## Pets

| Método | Endpoint | Descrição | Acesso |
|---|---|---|---|
| POST | `/pets` | Cadastra um novo pet (dono = responsável autenticado) | Responsável |
| GET | `/pets` | Lista pets (responsável vê só os seus; veterinário vê todos) | Autenticado |
| GET | `/pets/{id}` | Busca pet por ID | Autenticado (com checagem de posse) |
| GET | `/pets?page=0&size=10&sort=nomePet,asc` | Busca com paginação/ordenação | Autenticado |
| GET | `/pets/nome?nomePet=Thor` | Busca pets por nome | Autenticado |
| GET | `/pets/especie?especie=Cachorro` | Busca pets por espécie | Autenticado |
| GET | `/pets/raca?raca=Pug` | Busca pets por raça | Autenticado |
| PUT | `/pets/{id}` | Atualiza um pet | O responsável dono do pet |
| DELETE | `/pets/{id}` | Remove um pet | O responsável dono do pet |

## Diário

| Método | Endpoint | Descrição | Acesso |
|---|---|---|---|
| POST | `/diarios` | Cria um registro de diário para um pet | Responsável dono do pet |
| GET | `/diarios/pet/{idPet}` | Lista o histórico de diário de um pet (mais recente primeiro) | Autenticado (com checagem de posse) |
| GET | `/diarios/{id}` | Busca um registro específico | Autenticado (com checagem de posse) |
| PUT | `/diarios/{id}` | Atualiza um registro | O responsável dono do pet |
| DELETE | `/diarios/{id}` | Remove um registro | O responsável dono do pet |

## Swagger

| Recurso | Endpoint |
|---|---|
| Swagger UI | `/swagger-ui.html` |

---

# Dockerfile da API

Multi-stage build com `jlink`, gerando um runtime Java customizado e enxuto:

- **Build**: `maven:3-eclipse-temurin-25-alpine` compila e gera, via `jdeps`/`jlink`, um JRE mínimo contendo só os módulos que a aplicação realmente usa.
- **Runtime**: `alpine:3.21` mesma base do estágio de build, evitando incompatibilidade entre glibc e musl (um JRE gerado em ambiente glibc não roda em Alpine sem essa consistência).
- **Sem privilégio root**: usuário dedicado `chupinuser`/`chupingroup`, atendendo ao requisito do checkpoint de o container da aplicação nunca rodar como root/admin.

---

# Docker Compose (ambiente local)

O `compose.yml` usa as **mesmas imagens publicadas** que o deploy na nuvem (nenhuma build local), 
garantindo que o comportamento local seja idêntico ao que roda no ACI:

- `mysql`: `docker.io/vitordalmagro/chupinvet-mysql:v1`  imagem customizada (ver seção "Imagem customizada do MySQL" abaixo).
- `app`: `docker.io/vitordalmagro/chupinvet-api:mysql-v1`.

Variáveis sensíveis (`JWT_SECRET`) ficam com valor fixo só para uso local em produção (ACI), vêm do Key Vault.

---

# Infraestrutura na Azure (ACR + ACI)

Todos os recursos são criados via **Azure CLI**, através dos scripts na pasta `scripts/`, nesta ordem:

```text
scripts/
├── 00-config.sh                     => variáveis compartilhadas
├── 01-criar-acr.sh                  => Resource Group + Azure Container Registry
├── 02-publicar-imagens.sh           => puxa as imagens do Docker Hub e republica no ACR (roda LOCAL)
├── 03-criar-storage.sh              => Storage Account + Azure Files (persistência do MySQL)
├── 04-criar-keyvault.sh             => Key Vault + segredos (senha do banco, JWT secret)
├── 05-implantar-container-group.sh  => sobe o Container Group (API + MySQL)
└── 06-LIMPAR-RECURSOS.sh            => apaga TUDO (Resource Group + purge do Key Vault)
```

- Os scripts `01`, `03`, `04` e `05` rodam no **Cloud Shell** (só precisam do Azure CLI).
- O `02-publicar-imagens.sh` roda na **máquina local** (precisa do Podman ou Docker instalado), é o único passo que não usa Azure CLI.

## Pré-requisitos

- Conta Azure com assinatura ativa
- Podman (ou Docker) instalado localmente
- Azure CLI (`az login` já autenticado, ou usar o Cloud Shell do portal)

## Passo a passo

```bash
# 1. Clone o repositório
git clone https://github.com/ChupinVet/DevOpsChallenge.git
cd DevOpsChallenge

# 2. No Cloud Shell (ou local, com az CLI autenticado): cria o Resource Group e o ACR
bash scripts/01-criar-acr.sh

# 3. Na sua máquina local: puxa as imagens do Docker Hub e publica no ACR privado
bash scripts/02-publicar-imagens.sh
# Usa o Azure CLI já autenticado (az login) para pegar as credenciais do ACR sozinho

# 4. No Cloud Shell: cria o Storage Account/File Share e o Key Vault
bash scripts/03-criar-storage.sh
bash scripts/04-criar-keyvault.sh

# 5. No Cloud Shell: implanta o Container Group (API + MySQL)
bash scripts/05-implantar-container-group.sh
```

O script final imprime a URL pública da API. Como o IP do ACI pode mudar a cada novo deploy, para consultá-lo depois é só rodar:

```bash
az container show \
  --resource-group rg-chupinvet-aci-566052 \
  --name aci-chupinvet-566052 \
  --query ipAddress.ip \
  --output tsv
```

## Removendo tudo depois

```bash
bash scripts/06-LIMPAR-RECURSOS.sh
```

Pede confirmação (digitar o nome do Resource Group) antes de apagar. 
Remove o Resource Group inteiro e purga o Key Vault, sem isso, o nome do Key Vault fica bloqueado por até 90 dias (soft-delete do Azure) e um próximo `04-criar-keyvault.sh` falharia.

## Testando a aplicação na nuvem

```bash
# Swagger
http://<IP_DA_API>:8080/swagger-ui.html

# Endpoint público (sem autenticação)
curl http://<IP_DA_API>:8080/veterinarios

# Login com um dos usuários de exemplo do script_bd.sql (senha: senha123)
curl -X POST http://<IP_DA_API>:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"ana.costa@teste.com","senha":"senha123"}'
```

## Consultando o banco diretamente (evidência de persistência)

```bash
az container exec \
  --resource-group rg-chupinvet-aci-566052 \
  --name aci-chupinvet-566052 \
  --container-name mysql \
  --exec-command "/bin/bash"

# dentro do container:
mysql -uroot -p
# (cola a senha obtida com o comando abaixo, quando solicitado)
```

Para obter a senha do banco (guardada no Key Vault, nunca no código):

```bash
az keyvault secret show \
  --vault-name kv-chupinvet-566052 \
  --name db-password \
  --query value \
  --output tsv
```

---

# Como Executar Localmente (sem Azure)

```bash
git clone https://github.com/ChupinVet/DevOpsChallenge.git
cd DevOpsChallenge

# Com Podman:
podman-compose -f compose.yml up -d

# Ou com Docker:
docker compose -f compose.yml up -d
```

Acesse:
```text
http://localhost:8080
http://localhost:8080/swagger-ui.html
```

---

# Disciplina

DevOps Tools e Cloud Computing

FIAP