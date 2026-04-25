package com.dominium.backend.application.reservas.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class FilaRequestDTO {
    private Long areaComumId;
    private Long usuarioId;
    private LocalDateTime dataDesejada;
}