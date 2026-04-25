CREATE TABLE IF NOT EXISTS usuarios (
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

CREATE TABLE IF NOT EXISTS orcamentos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ano INTEGER NOT NULL UNIQUE,
    valor_total DECIMAL(19, 2) NOT NULL,
    valor_gasto DECIMAL(19, 2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS despesas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    descricao VARCHAR(255) NOT NULL,
    valor DECIMAL(19, 2) NOT NULL,
    data DATE NOT NULL,
    categoria VARCHAR(50) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    orcamento_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_despesa_orcamento FOREIGN KEY (orcamento_id) REFERENCES orcamentos(id)
);

CREATE TABLE multas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ocorrencia_id BIGINT,
    unidade_id BIGINT NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    valor DECIMAL(15,2) NOT NULL,
    valor_base DECIMAL(15,2),
    tipo_valor VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL,
    reincidencia INTEGER NOT NULL DEFAULT 0,
    data_criacao TIMESTAMP NOT NULL,
    data_pagamento TIMESTAMP,
    valor_pago DECIMAL(15,2),
    updated_at TIMESTAMP,
    justificativa_contestacao TEXT,
    data_contestacao TIMESTAMP,

    CONSTRAINT fk_multa_unidade
        FOREIGN KEY (unidade_id)
        REFERENCES unidades(id)
);

CREATE TABLE IF NOT EXISTS recurso_multa (
    id UUID PRIMARY KEY,
    multa_id UUID NOT NULL,
    morador_id UUID NOT NULL,
    motivo TEXT NOT NULL,
    data_solicitacao TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    justificativa_sindico TEXT,
    data_decisao TIMESTAMP
);