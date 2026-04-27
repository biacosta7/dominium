package com.dominium.backend.application.multa.dto;

import com.dominium.backend.domain.multa.StatusMulta;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateMultaStatusRequestDTO {

    @NotNull(message = "O status é obrigatório.")
    private StatusMulta status;
}