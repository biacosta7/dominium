package com.dominium.backend.application.ocorrencia.dto;

import java.math.BigDecimal;

public class EncerrarOcorrenciaRequestDTO {
    private String penalidade;
    private String observacao;
    private BigDecimal valorMulta;

    public EncerrarOcorrenciaRequestDTO() {}

    public String getPenalidade() { return penalidade; }
    public void setPenalidade(String penalidade) { this.penalidade = penalidade; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    public BigDecimal getValorMulta() { return valorMulta; }
    public void setValorMulta(BigDecimal valorMulta) { this.valorMulta = valorMulta; }
}
