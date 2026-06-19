package br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.strategy;

import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.Despesa;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.Orcamento;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.StatusDespesa;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.TipoDespesa;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component("rigida")
public class PoliticaFinanceiraRigidaStrategy implements PoliticaFinanceiraStrategy {
    private static final BigDecimal LIMITE_ORDINARIA_AUDITORIA = new BigDecimal("10000.00");

    @Override
    public StatusDespesa determinarStatusInicial(Despesa despesa, Orcamento orcamento) {
        if (despesa.getTipo() == TipoDespesa.EXTRAORDINARIA) {
            return StatusDespesa.PENDENTE;
        }
        if (despesa.getValor().compareTo(LIMITE_ORDINARIA_AUDITORIA) > 0) {
            return StatusDespesa.PENDENTE;
        }
        return StatusDespesa.APROVADA;
    }
}
