package br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.strategy;

import java.math.BigDecimal;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.StatusDespesa;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.Despesa;

public class AprovacaoDespesaOrdinariaStrategy implements AprovacaoDespesaStrategy {
    private static final BigDecimal LIMITE_APROVACAO_ORDINARIA = new BigDecimal("10000.00");

    @Override
    public StatusDespesa determinarStatusInicial(Despesa despesa) {
        if (despesa.getValor().compareTo(LIMITE_APROVACAO_ORDINARIA) > 0) {
            return StatusDespesa.PENDENTE;
        }
        return StatusDespesa.APROVADA;
    }
}
