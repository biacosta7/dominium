package br.com.cesar.gestaoCondominial.aplicacao.dominium.ocorrencia.dto;

import jakarta.validation.constraints.NotBlank;

public class AtualizarStatusOcorrenciaRequestDTO {
    @NotBlank
    private String status;

    public AtualizarStatusOcorrenciaRequestDTO() {}

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
