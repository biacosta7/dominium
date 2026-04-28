package br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.exception;

public class CapacidadeExcedidaException extends ReservaException {
    public CapacidadeExcedidaException() {
        super("A capacidade máxima da área comum foi excedida para o período selecionado.");
    }
}
