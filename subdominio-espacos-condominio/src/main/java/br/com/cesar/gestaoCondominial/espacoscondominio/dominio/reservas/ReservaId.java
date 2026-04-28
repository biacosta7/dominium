package br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas;

import java.util.UUID;
import br.com.cesar.gestaoCondominial.dominio.dominium.valueobjects.ValueObjectId;
import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.DomainException;

public class ReservaId extends ValueObjectId<String> {

    private ReservaId(String valor) {
        super(valor);
        if (valor.isBlank()) {
            throw new DomainException("ID da reserva não pode ser vazio");
        }
    }

    public static ReservaId novo() {
        return new ReservaId(UUID.randomUUID().toString());
    }

    public static ReservaId de(String valor) {
        return new ReservaId(valor);
    }
}
