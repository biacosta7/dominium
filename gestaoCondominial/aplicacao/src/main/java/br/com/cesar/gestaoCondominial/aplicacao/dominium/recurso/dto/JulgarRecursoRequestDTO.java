package br.com.cesar.gestaoCondominial.aplicacao.dominium.recurso.dto;

import br.com.cesar.gestaoCondominial.dominio.dominium.recurso.StatusRecurso;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class JulgarRecursoRequestDTO {
    private StatusRecurso status;
    private String justificativa;
    private boolean cancelarMulta;
    private BigDecimal novoValorMulta;
}