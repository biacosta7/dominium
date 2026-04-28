package br.com.cesar.gestaoCondominial.financeiro.dominio.taxa;

import br.com.cesar.gestaoCondominial.dominio.dominium.valueobjects.ValueObjectId;

public class TaxaId extends ValueObjectId<Long> {
    public TaxaId(Long valor) {
        super(valor);
    }
}