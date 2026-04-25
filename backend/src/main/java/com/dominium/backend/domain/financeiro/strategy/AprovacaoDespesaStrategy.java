package com.dominium.backend.domain.financeiro.strategy;

import com.dominium.backend.domain.financeiro.Despesa;
import com.dominium.backend.domain.financeiro.StatusDespesa;

public interface AprovacaoDespesaStrategy {
    StatusDespesa determinarStatusInicial(Despesa despesa);
}
