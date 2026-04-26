package com.dominium.backend.domain.reservas.exception;

public class ConflitoReservaException extends ReservaException {
    public ConflitoReservaException() {
        super("Já existe uma reserva ou conflito de horário para este período.");
    }
}
