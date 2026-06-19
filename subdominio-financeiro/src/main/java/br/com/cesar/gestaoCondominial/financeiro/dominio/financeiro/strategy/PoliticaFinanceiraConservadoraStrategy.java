package br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.strategy;

import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.Despesa;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.Orcamento;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.StatusDespesa;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.TipoDespesa;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component("conservadora")
@Primary
public class PoliticaFinanceiraConservadoraStrategy implements PoliticaFinanceiraStrategy {
    private static final BigDecimal LIMITE_ASSEMBLEIA = new BigDecimal("5000.00");

    @Override
    public StatusDespesa determinarStatusInicial(Despesa despesa, Orcamento orcamento) {
        if (despesa.getTipo() == TipoDespesa.EXTRAORDINARIA && despesa.getValor().compareTo(LIMITE_ASSEMBLEIA) > 0) {
            return StatusDespesa.PENDENTE;
        }
        return StatusDespesa.APROVADA;
    }
}
