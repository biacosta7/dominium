package br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.strategy;

import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.Despesa;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.Orcamento;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.StatusDespesa;
import org.springframework.stereotype.Component;

@Component("flexivel")
public class PoliticaFinanceiraFlexivelStrategy implements PoliticaFinanceiraStrategy {
    @Override
    public StatusDespesa determinarStatusInicial(Despesa despesa, Orcamento orcamento) {
        return StatusDespesa.APROVADA;
    }
}
