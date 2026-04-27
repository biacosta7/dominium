package br.com.cesar.gestaoCondominial.dominio.dominium.taxa;

import com.dominium.backend.domain.shared.valueobjects.ValueObjectId;

public class TaxaId extends ValueObjectId<Long> {
    public TaxaId(Long valor) {
        super(valor);
    }
}