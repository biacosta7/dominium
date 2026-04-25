package com.dominium.backend.domain.usuario;

import com.dominium.backend.domain.shared.valueobjects.ValueObjectId;

public class UsuarioId extends ValueObjectId<Long> {
    public UsuarioId(Long valor) {
        super(valor);
    }
}