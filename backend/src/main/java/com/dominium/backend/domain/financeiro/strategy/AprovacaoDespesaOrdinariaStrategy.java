package com.dominium.backend.domain.financeiro.strategy;

import com.dominium.backend.domain.financeiro.Despesa;
import com.dominium.backend.domain.financeiro.StatusDespesa;

public class AprovacaoDespesaOrdinariaStrategy implements AprovacaoDespesaStrategy {
    @Override
    public StatusDespesa determinarStatusInicial(Despesa despesa) {
        return StatusDespesa.APROVADA;
    }
}
