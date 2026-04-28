package br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.exception;

public class ConflitoReservaException extends ReservaException {
    public ConflitoReservaException() {
        super("Já existe uma reserva ou conflito de horário para este período.");
    }
}
