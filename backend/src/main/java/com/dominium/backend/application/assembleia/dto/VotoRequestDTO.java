package com.dominium.backend.application.assembleia.dto;

import lombok.Data;

@Data
public class VotoRequestDTO {
    private Long unidadeId;
    private String tipoVoto;
}