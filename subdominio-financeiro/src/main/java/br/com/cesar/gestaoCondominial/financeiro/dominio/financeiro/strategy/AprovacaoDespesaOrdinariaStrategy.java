package br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.strategy;

import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.StatusDespesa;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.Despesa;

public class AprovacaoDespesaOrdinariaStrategy implements AprovacaoDespesaStrategy {
    @Override
    public StatusDespesa determinarStatusInicial(Despesa despesa) {
        return StatusDespesa.APROVADA;
    }
}
