package br.com.cesar.gestaoCondominial.dominio.dominium.financeiro.strategy;

import br.com.cesar.gestaoCondominial.dominio.dominium.financeiro.StatusDespesa;
import br.com.cesar.gestaoCondominial.dominio.dominium.financeiro.Despesa;

public interface AprovacaoDespesaStrategy {
    StatusDespesa determinarStatusInicial(Despesa despesa);
}
