package br.com.cesar.gestaoCondominial.financeiro.dominio.multa.strategy;

import java.math.BigDecimal;

public interface CalculoMultaStrategy {
    BigDecimal calcular(BigDecimal valorBase, long reincidencias);
}
