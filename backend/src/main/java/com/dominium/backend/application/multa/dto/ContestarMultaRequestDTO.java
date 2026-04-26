package com.dominium.backend.application.multa.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ContestarMultaRequestDTO {

    @NotBlank(message = "A justificativa é obrigatória.")
    private String justificativa;
}