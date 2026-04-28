package br.com.cesar.gestaoCondominial.aplicacao.dominium.multa.dto;

import br.com.cesar.gestaoCondominial.dominio.dominium.multa.StatusMulta;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateMultaStatusRequestDTO {

    @NotNull(message = "O status é obrigatório.")
    private StatusMulta status;
}