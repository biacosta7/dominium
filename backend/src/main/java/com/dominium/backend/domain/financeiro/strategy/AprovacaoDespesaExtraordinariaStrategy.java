package com.dominium.backend.domain.financeiro.strategy;

import java.math.BigDecimal;
import com.dominium.backend.domain.financeiro.Despesa;
import com.dominium.backend.domain.financeiro.StatusDespesa;

public class AprovacaoDespesaExtraordinariaStrategy implements AprovacaoDespesaStrategy {
    private static final BigDecimal LIMITE_APROVACAO_ASSEMBLEIA = new BigDecimal("5000.00");

    @Override
    public StatusDespesa determinarStatusInicial(Despesa despesa) {
        if (despesa.getValor().compareTo(LIMITE_APROVACAO_ASSEMBLEIA) > 0) {
            return StatusDespesa.PENDENTE;
        }
        return StatusDespesa.APROVADA;
    }
}
