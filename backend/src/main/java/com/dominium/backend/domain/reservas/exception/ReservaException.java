package com.dominium.backend.domain.reservas.exception;

import com.dominium.backend.domain.shared.exceptions.DomainException;

public class ReservaException extends DomainException {
    public ReservaException(String message) {
        super(message);
    }
}
