

CREATE DATABASE IF NOT EXISTS chupinvet
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE chupinvet;


-- Tabela: usuario
-- Identidade base de qualquer pessoa no sistema (Responsável ou Veterinário).
-- Não guarda "tipo" — isso é derivado pela existência de registro em responsavel ou veterinario.

CREATE TABLE usuario (
    id_usuario      BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Identificador único do usuário',
    nm_usuario      VARCHAR(100) NOT NULL COMMENT 'Nome completo',
    ds_email        VARCHAR(150) NOT NULL COMMENT 'E-mail, usado como login',
    ds_senha        VARCHAR(100) NOT NULL COMMENT 'Senha com hash BCrypt',
    num_cpf         VARCHAR(11)  NOT NULL COMMENT 'CPF (11 dígitos)',
    nm_estado       VARCHAR(50)  COMMENT 'Estado (UF)',
    nm_cidade       VARCHAR(80)  COMMENT 'Cidade',
    num_telefone    VARCHAR(15)  NOT NULL COMMENT 'Telefone principal',
    UNIQUE KEY uk_usuario_email (ds_email),
    UNIQUE KEY uk_usuario_cpf (num_cpf)
) ENGINE=InnoDB COMMENT='Identidade base dos usuários do sistema';


-- Tabela: responsavel
-- Dados específicos de quem é responsável por pets. Associação 1:1 com usuario (composição, não herança).

CREATE TABLE responsavel (
    id_responsavel          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Identificador único do responsável',
    dt_nascimento            DATE         COMMENT 'Data de nascimento',
    tp_genero                VARCHAR(20)  COMMENT 'Gênero',
    tp_residencia            VARCHAR(20)  COMMENT 'Tipo de residência (Casa, Apartamento, etc.)',
    num_telefone_secundario  VARCHAR(15)  COMMENT 'Telefone secundário (opcional)',
    usuario_id_usuario       BIGINT NOT NULL COMMENT 'FK para usuario (1:1)',
    UNIQUE KEY uk_responsavel_usuario (usuario_id_usuario),
    CONSTRAINT fk_responsavel_usuario
        FOREIGN KEY (usuario_id_usuario) REFERENCES usuario (id_usuario)
        ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='Responsáveis pelos pets';


-- Tabela: veterinario
-- Dados específicos de profissionais veterinários. Associação 1:1 com usuario.

CREATE TABLE veterinario (
    id_veterinario        BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Identificador único do veterinário',
    num_crmv              VARCHAR(10)  NOT NULL COMMENT 'Número do CRMV',
    ds_especialidade      VARCHAR(80)  NOT NULL COMMENT 'Especialidade',
    qtd_anos_experiencia  INT          COMMENT 'Anos de experiência',
    ds_disponibilidade    VARCHAR(100) COMMENT 'Disponibilidade de atendimento',
    tp_servico            VARCHAR(100) COMMENT 'Tipo de serviço prestado',
    nm_clinica            VARCHAR(100) COMMENT 'Nome da clínica onde atende',
    ds_bio                VARCHAR(500) COMMENT 'Biografia/descrição profissional',
    usuario_id_usuario    BIGINT NOT NULL COMMENT 'FK para usuario (1:1)',
    UNIQUE KEY uk_veterinario_crmv (num_crmv),
    UNIQUE KEY uk_veterinario_usuario (usuario_id_usuario),
    CONSTRAINT fk_veterinario_usuario
        FOREIGN KEY (usuario_id_usuario) REFERENCES usuario (id_usuario)
        ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='Veterinários cadastrados no sistema';


-- Tabela: pet (tabela CORE nº1)
-- Um pet pertence a exatamente um responsável (N:1).

CREATE TABLE pet (
    id_pet                     BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Identificador único do pet',
    nm_pet                     VARCHAR(80) NOT NULL COMMENT 'Nome do pet',
    tp_especie                 VARCHAR(50) NOT NULL COMMENT 'Espécie (Cachorro, Gato, etc.)',
    nm_raca                    VARCHAR(50) COMMENT 'Raça',
    qtd_idade_pet               INT         COMMENT 'Idade em anos',
    vl_peso                     DECIMAL(5,2) COMMENT 'Peso atual em kg',
    responsavel_id_responsavel BIGINT NOT NULL COMMENT 'FK para responsavel (N:1)',
    CONSTRAINT fk_pet_responsavel
        FOREIGN KEY (responsavel_id_responsavel) REFERENCES responsavel (id_responsavel)
        ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='Pets cadastrados, vinculados a um responsável';


-- Tabela: diario (tabela CORE nº2)
-- Um registro de diário pertence a exatamente um pet (N:1).


CREATE TABLE diario (
    id_diario           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Identificador único do registro',
    dt_registro          DATE NOT NULL COMMENT 'Data do registro',
    tp_humor             VARCHAR(50) NOT NULL COMMENT 'Humor observado do pet',
    tp_alimentacao       VARCHAR(50) NOT NULL COMMENT 'Alimentação do dia',
    tp_agua              VARCHAR(50) NOT NULL COMMENT 'Consumo de água do dia',
    ds_comportamento     VARCHAR(500) COMMENT 'Observações de comportamento',
    ds_sintomas          VARCHAR(500) COMMENT 'Sintomas observados, se houver',
    ds_observacoes       VARCHAR(500) COMMENT 'Observações gerais do responsável',
    vl_peso_registrado   DECIMAL(5,2) COMMENT 'Peso do pet no momento do registro',
    ds_insight_ia        VARCHAR(500) COMMENT 'Insight gerado por IA (preenchido em sprint futura)',
    pet_id_pet           BIGINT NOT NULL COMMENT 'FK para pet (N:1)',
    CONSTRAINT fk_diario_pet
        FOREIGN KEY (pet_id_pet) REFERENCES pet (id_pet)
        ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='Registros de diário de saúde/comportamento dos pets';


-- Carga de dados de exemplo
INSERT INTO usuario (nm_usuario, ds_email, ds_senha, num_cpf, nm_estado, nm_cidade, num_telefone) VALUES
('Ana Beatriz Costa', 'ana.costa@teste.com', '$2b$10$xh.2gUkQRp0Vg2.tM9zwvOc3BCQ4s5B386S/QNnzVQxT03bWG19Yy', '11122233344', 'SP', 'Sao Paulo', '11987654321'),
('Bruno Ferreira Lima', 'bruno.lima@teste.com', '$2b$10$xh.2gUkQRp0Vg2.tM9zwvOc3BCQ4s5B386S/QNnzVQxT03bWG19Yy', '22233344455', 'RJ', 'Rio de Janeiro', '21987654321'),
('Carla Mendes Souza', 'carla.vet@teste.com', '$2b$10$xh.2gUkQRp0Vg2.tM9zwvOc3BCQ4s5B386S/QNnzVQxT03bWG19Yy', '33344455566', 'SP', 'Campinas', '19987654321');

INSERT INTO responsavel (dt_nascimento, tp_genero, tp_residencia, num_telefone_secundario, usuario_id_usuario) VALUES
('1995-03-22', 'Feminino', 'Apartamento', '11912345678', 1),
('1990-07-10', 'Masculino', 'Casa', NULL, 2);

INSERT INTO veterinario (num_crmv, ds_especialidade, qtd_anos_experiencia, ds_disponibilidade, tp_servico, nm_clinica, ds_bio, usuario_id_usuario) VALUES
('SP12345', 'Clinica Geral', 8, 'Segunda a Sexta, 8h-18h', 'Consulta', 'Clinica Pet Amigo', 'Veterinaria generalista com foco em caes e gatos.', 3);

INSERT INTO pet (nm_pet, tp_especie, nm_raca, qtd_idade_pet, vl_peso, responsavel_id_responsavel) VALUES
('Nasus', 'Cachorro', 'Golden Retriever', 6, 30.50, 1),
('Mimi', 'Gato', 'Siames', 3, 4.20, 2);

INSERT INTO diario (dt_registro, tp_humor, tp_alimentacao, tp_agua, ds_comportamento, ds_sintomas, ds_observacoes, vl_peso_registrado, pet_id_pet) VALUES
('2026-09-01', 'Feliz', 'Normal', 'Bebeu bem', 'Brincalhao, sem sinais de dor', NULL, 'Passeou 30 minutos no parque', 30.20, 1),
('2026-09-05', 'Calmo', 'Reduzida', 'Bebeu pouco', 'Dormiu mais que o normal', 'Leve espirro', 'Observar nos proximos dias', 4.10, 2);
