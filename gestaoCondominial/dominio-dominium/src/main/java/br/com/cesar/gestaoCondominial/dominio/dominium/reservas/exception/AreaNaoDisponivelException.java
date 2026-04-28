package br.com.cesar.gestaoCondominial.dominio.dominium.reservas.exception;

public class AreaNaoDisponivelException extends ReservaException {
    public AreaNaoDisponivelException() {
        super("A área comum selecionada não está disponível para reserva.");
    }
}
