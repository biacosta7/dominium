package br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.strategy;

import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.Despesa;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.Orcamento;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.StatusDespesa;

public interface PoliticaFinanceiraStrategy {
    StatusDespesa determinarStatusInicial(Despesa despesa, Orcamento orcamento);
}
