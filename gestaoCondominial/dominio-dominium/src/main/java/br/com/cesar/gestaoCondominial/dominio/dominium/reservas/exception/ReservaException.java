package br.com.cesar.gestaoCondominial.dominio.dominium.reservas.exception;

import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.DomainException;

public class ReservaException extends DomainException {
    public ReservaException(String message) {
        super(message);
    }
}
