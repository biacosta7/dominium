package com.dominium.backend.application.ocorrencia.dto;

public class OcorrenciaRequestDTO {
    private String descricao;
    private Long unidadeId;
    private boolean geraMulta;

    public OcorrenciaRequestDTO() {
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Long getUnidadeId() {
        return unidadeId;
    }

    public void setUnidadeId(Long unidadeId) {
        this.unidadeId = unidadeId;
    }

    public boolean isGeraMulta() {
        return geraMulta;
    }

    public void setGeraMulta(boolean geraMulta) {
        this.geraMulta = geraMulta;
    }
}