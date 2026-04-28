package br.com.cesar.gestaoCondominial.financeiro.aplicacao.multa.dto;

import br.com.cesar.gestaoCondominial.financeiro.dominio.multa.StatusMulta;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateMultaStatusRequestDTO {

    @NotNull(message = "O status é obrigatório.")
    private StatusMulta status;
}