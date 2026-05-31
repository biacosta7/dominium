package br.com.cesar.gestaoCondominial.financeiro.dominio.multa.strategy;

import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class CalculoMultaProgressivaStrategy implements CalculoMultaStrategy {

    @Override
    public BigDecimal calcular(BigDecimal valorBase, long reincidencias) {
        if (reincidencias == 0) {
            return valorBase;
        }

        BigDecimal percentual = BigDecimal.valueOf(0.10 * reincidencias);

        return valorBase.add(
                valorBase.multiply(percentual)
        );
    }
}
