REATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    telefone VARCHAR(255),
    cpf VARCHAR(14) UNIQUE,
    tipo VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS unidades (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero VARCHAR(50) NOT NULL,
    bloco VARCHAR(50) NOT NULL,
    proprietario_id BIGINT NOT NULL,
    inquilino_id BIGINT,
    status VARCHAR(50),
    saldo_devedor DECIMAL(19, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_proprietario FOREIGN KEY (proprietario_id) REFERENCES usuarios(id),
    CONSTRAINT fk_inquilino FOREIGN KEY (inquilino_id) REFERENCES usuarios(id),
    CONSTRAINT uk_numero_bloco UNIQUE(numero, bloco)
);

CREATE TABLE IF NOT EXISTS vinculos_morador (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    unidade_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_vinculo_unidade FOREIGN KEY (unidade_id) REFERENCES unidades(id),
    CONSTRAINT fk_vinculo_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

CREATE TABLE IF NOT EXISTS pauta (
    id BIGSERIAL PRIMARY KEY,
    assembleia_id BIGINT NOT NULL,
    titulo VARCHAR(255) NOT NULL,
    descricao TEXT,
    tipo_quorum VARCHAR(50)  NOT NULL,
    tipo_maioria VARCHAR(50)  NOT NULL,
    status VARCHAR(50)  NOT NULL DEFAULT 'ABERTA',
    resultado VARCHAR(50)  NOT NULL DEFAULT 'EM_ANDAMENTO'
    );

CREATE TABLE IF NOT EXISTS voto (
    id BIGSERIAL PRIMARY KEY,
    pauta_id BIGINT NOT NULL REFERENCES pauta(id),
    unidade_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    opcao_voto VARCHAR(50) NOT NULL,

    -- Uma unidade só pode votar uma vez por pauta
    CONSTRAINT uk_voto_pauta_unidade UNIQUE (pauta_id, unidade_id)
    );

CREATE TABLE IF NOT EXISTS reserva (
    id  VARCHAR(36) PRIMARY KEY,  -- UUID gerado pelo domínio
    area_comum_id BIGINT NOT NULL,
    unidade_id BIGINT NOT NULL REFERENCES unidade(id),
    usuario_id BIGINT NOT NULL REFERENCES usuario(id),
    data_reserva DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fim TIME NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDENTE'
    );