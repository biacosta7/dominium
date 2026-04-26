package com.dominium.backend.domain.multa;
import java.util.Objects;
import java.util.UUID;

import com.dominium.backend.domain.shared.valueobjects.ValueObjectId;

public class MultaId extends ValueObjectId<Long> {
    public MultaId(Long valor){
        super(valor);
    }
}
