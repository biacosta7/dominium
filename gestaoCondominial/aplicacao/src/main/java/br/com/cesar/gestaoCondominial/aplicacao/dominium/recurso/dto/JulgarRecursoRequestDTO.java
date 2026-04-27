package com.dominium.backend.application.recurso.dto;

import com.dominium.backend.domain.recurso.StatusRecurso;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class JulgarRecursoRequestDTO {
    private StatusRecurso status;
    private String justificativa;
    private boolean cancelarMulta;
    private BigDecimal novoValorMulta;
}