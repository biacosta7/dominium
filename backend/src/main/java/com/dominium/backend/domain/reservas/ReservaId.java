package com.dominium.backend.domain.reservas;

import java.util.UUID;
import com.dominium.backend.domain.shared.valueobjects.ValueObjectId;

public class ReservaId extends ValueObjectId<String> {

    private ReservaId(String valor) {
        super(valor);
        if (valor.isBlank()) {
            throw new com.dominium.backend.domain.shared.exceptions.DomainException("ID da reserva não pode ser vazio");
        }
    }

    public static ReservaId novo() {
        return new ReservaId(UUID.randomUUID().toString());
    }

    public static ReservaId de(String valor) {
        return new ReservaId(valor);
    }
}
