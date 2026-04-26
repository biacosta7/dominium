package com.dominium.backend.domain.taxa;

import com.dominium.backend.domain.shared.valueobjects.ValueObjectId;

public class TaxaId extends ValueObjectId<Long> {
    public TaxaId(Long valor) {
        super(valor);
    }
}