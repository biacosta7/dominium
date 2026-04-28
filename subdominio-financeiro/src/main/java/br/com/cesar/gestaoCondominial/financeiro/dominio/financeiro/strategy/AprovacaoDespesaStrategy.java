package br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.strategy;

import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.StatusDespesa;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.Despesa;

public interface AprovacaoDespesaStrategy {
    StatusDespesa determinarStatusInicial(Despesa despesa);
}
