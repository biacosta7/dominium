package com.dominium.backend.application.assembleia.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class AssembleiaResponseDTO {
    private String id;
    private LocalDateTime dataHora;
    private String local;
    private boolean concluida;
}