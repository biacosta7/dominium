package com.dominium.backend.domain.assembleia;

import com.dominium.backend.domain.shared.valueobjects.ValueObjectId;

import java.util.Objects;
import java.util.UUID;

public class AssembleiaId extends ValueObjectId<Long> {

    public AssembleiaId(Long valor) {
        super(valor);
    }
}

