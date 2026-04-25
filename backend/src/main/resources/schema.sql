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

CREATE TABLE IF NOT EXISTS funcionarios (
    id VARCHAR(36) PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    cpf VARCHAR(14) NOT NULL,
    email VARCHAR(255),
    telefone VARCHAR(20),
    tipo_vinculo VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    contrato_inicio DATE NOT NULL,
    contrato_fim DATE NOT NULL,
    valor_mensal DECIMAL(19, 2),
    sindico_id BIGINT NOT NULL,
    data_cadastro TIMESTAMP NOT NULL,
    CONSTRAINT fk_funcionario_sindico FOREIGN KEY (sindico_id) REFERENCES usuarios(id)
);

CREATE TABLE IF NOT EXISTS ordens_servico (
    id VARCHAR(36) PRIMARY KEY,
    descricao VARCHAR(500) NOT NULL,
    funcionario_id VARCHAR(36) NOT NULL,
    status VARCHAR(50) NOT NULL,
    data_inicio DATE NOT NULL,
    data_fim DATE NOT NULL,
    CONSTRAINT fk_os_funcionario FOREIGN KEY (funcionario_id) REFERENCES funcionarios(id)
);

CREATE TABLE IF NOT EXISTS avaliacoes_funcionario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    funcionario_id VARCHAR(36) NOT NULL,
    positiva BOOLEAN NOT NULL,
    comentario VARCHAR(500),
    data DATE NOT NULL,
    CONSTRAINT fk_avaliacao_funcionario FOREIGN KEY (funcionario_id) REFERENCES funcionarios(id)
);
