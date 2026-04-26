package com.dominium.backend.application.recurso.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class AbrirRecursoRequestDTO {
    private Long multaId;
    private UUID moradorId;
    private String motivo;
}