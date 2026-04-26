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
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
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

CREATE TABLE IF NOT EXISTS areas_comuns (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    capacidade_maxima INTEGER NOT NULL,
    status VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS reservas (
    id VARCHAR(36) PRIMARY KEY,
    area_comum_id BIGINT NOT NULL,
    unidade_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    data_reserva DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fim TIME NOT NULL,
    status VARCHAR(50) NOT NULL,
    data_expira_confirmacao TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reserva_area FOREIGN KEY (area_comum_id) REFERENCES areas_comuns(id),
    CONSTRAINT fk_reserva_unidade FOREIGN KEY (unidade_id) REFERENCES unidades(id),
    CONSTRAINT fk_reserva_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

CREATE TABLE IF NOT EXISTS fila_espera (
    id VARCHAR(36) PRIMARY KEY,
    area_comum_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    data_desejada DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fim TIME NOT NULL,
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) NOT NULL,
    CONSTRAINT fk_fila_area FOREIGN KEY (area_comum_id) REFERENCES areas_comuns(id),
    CONSTRAINT fk_fila_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
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

CREATE TABLE IF NOT EXISTS assembleias (
    id VARCHAR(36) PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    data_hora TIMESTAMP NOT NULL,
    local VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    sindico_id BIGINT NOT NULL,
    data_criacao TIMESTAMP NOT NULL,
    CONSTRAINT fk_assembleia_sindico FOREIGN KEY (sindico_id) REFERENCES usuarios(id)
    );


CREATE TABLE IF NOT EXISTS notificacoes_assembleia (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    assembleia_id VARCHAR(36) NOT NULL,
    usuario_id BIGINT NOT NULL,
    notificado_em TIMESTAMP NOT NULL,
    CONSTRAINT fk_notificacao_assembleia FOREIGN KEY (assembleia_id) REFERENCES assembleias(id),
    CONSTRAINT fk_notificacao_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
    );

CREATE TABLE IF NOT EXISTS ocorrencias (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    descricao TEXT NOT NULL,
    unidade_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    data_registro TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL,
    penalidade VARCHAR(50),
    observacao_sindico TEXT,
    CONSTRAINT fk_ocorrencia_unidade FOREIGN KEY (unidade_id) REFERENCES unidades(id),
    CONSTRAINT fk_ocorrencia_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
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
    multa_id BIGINT NOT NULL,
    morador_id BIGINT NOT NULL,
    motivo TEXT NOT NULL,
    data_solicitacao TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    justificativa_sindico TEXT,
    data_decisao TIMESTAMP,
    
    CONSTRAINT fk_recurso_multa FOREIGN KEY (multa_id) REFERENCES multas(id),
    CONSTRAINT fk_recurso_usuario FOREIGN KEY (morador_id) REFERENCES usuarios(id)
);

CREATE TABLE IF NOT EXISTS historico_titularidade (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    unidade_id BIGINT NOT NULL,
    proprietario_anterior_id BIGINT NOT NULL,
    novo_proprietario_id BIGINT NOT NULL,
    data_transferencia TIMESTAMP NOT NULL,
    CONSTRAINT fk_hist_unidade FOREIGN KEY (unidade_id) REFERENCES unidades(id),
    CONSTRAINT fk_hist_prop_ant FOREIGN KEY (proprietario_anterior_id) REFERENCES usuarios(id),
    CONSTRAINT fk_hist_prop_novo FOREIGN KEY (novo_proprietario_id) REFERENCES usuarios(id)
);

CREATE TABLE taxa_condominial (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    unidade_id BIGINT NOT NULL,
    valor_base DECIMAL(10,2) NOT NULL,
    valor_multas DECIMAL(10,2) NOT NULL,
    valor_total DECIMAL(10,2) NOT NULL,
    data_vencimento DATE NOT NULL,
    data_pagamento TIMESTAMP,
    status VARCHAR(20) NOT NULL
);