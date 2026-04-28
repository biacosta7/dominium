package br.com.cesar.gestaoCondominial.financeiro.aplicacao.recurso.dto;

import br.com.cesar.gestaoCondominial.financeiro.dominio.recurso.StatusRecurso;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class JulgarRecursoRequestDTO {
    private StatusRecurso status;
    private String justificativa;
    private boolean cancelarMulta;
    private BigDecimal novoValorMulta;
}