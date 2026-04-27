package com.dominium.backend.application.ocorrencia.dto;

import java.time.LocalDateTime;

public class OcorrenciaResponseDTO {
    private Long id;
    private String descricao;
    private Long unidadeId;
    private Long relatorId;
    private String relatorNome;
    private LocalDateTime dataRegistro;
    private String status;
    private String penalidade;
    private String observacaoSindico;

    public OcorrenciaResponseDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Long getUnidadeId() { return unidadeId; }
    public void setUnidadeId(Long unidadeId) { this.unidadeId = unidadeId; }

    public Long getRelatorId() { return relatorId; }
    public void setRelatorId(Long relatorId) { this.relatorId = relatorId; }

    public String getRelatorNome() { return relatorNome; }
    public void setRelatorNome(String relatorNome) { this.relatorNome = relatorNome; }

    public LocalDateTime getDataRegistro() { return dataRegistro; }
    public void setDataRegistro(LocalDateTime dataRegistro) { this.dataRegistro = dataRegistro; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPenalidade() { return penalidade; }
    public void setPenalidade(String penalidade) { this.penalidade = penalidade; }

    public String getObservacaoSindico() { return observacaoSindico; }
    public void setObservacaoSindico(String observacaoSindico) { this.observacaoSindico = observacaoSindico; }
}
