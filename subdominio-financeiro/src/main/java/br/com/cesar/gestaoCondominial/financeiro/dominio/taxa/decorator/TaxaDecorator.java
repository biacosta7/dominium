package br.com.cesar.gestaoCondominial.financeiro.dominio.taxa.decorator;

import java.math.BigDecimal;

public final class TaxaDecorator {
    private TaxaDecorator() {}

    @FunctionalInterface
    public interface Calculo {
        BigDecimal calcular();
    }

    public static final class Multas implements Calculo {
        private final Calculo calculoDecorado;
        private final BigDecimal multas;

        public Multas(Calculo calculoDecorado, BigDecimal multas) {
            this.calculoDecorado = calculoDecorado;
            this.multas = multas != null ? multas : BigDecimal.ZERO;
        }

        @Override
        public BigDecimal calcular() {
            return calculoDecorado.calcular().add(multas);
        }
    }
}
