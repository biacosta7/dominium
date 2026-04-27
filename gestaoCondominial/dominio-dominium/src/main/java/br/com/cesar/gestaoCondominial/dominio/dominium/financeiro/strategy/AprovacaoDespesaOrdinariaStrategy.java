package br.com.cesar.gestaoCondominial.dominio.dominium.financeiro.strategy;

import br.com.cesar.gestaoCondominial.dominio.dominium.financeiro.StatusDespesa;
import br.com.cesar.gestaoCondominial.dominio.dominium.financeiro.Despesa;

public class AprovacaoDespesaOrdinariaStrategy implements AprovacaoDespesaStrategy {
    @Override
    public StatusDespesa determinarStatusInicial(Despesa despesa) {
        return StatusDespesa.APROVADA;
    }
}
