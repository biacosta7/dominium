package com.dominium.backend.domain.governanca.voto;

import com.dominium.backend.domain.governanca.pauta.PautaId;
import com.dominium.backend.domain.shared.valueobjects.ValueObjectId;

import java.util.Objects;

public class VotoId extends ValueObjectId<Long> {

    public VotoId(Long valor){
        super(valor);
    }
}
