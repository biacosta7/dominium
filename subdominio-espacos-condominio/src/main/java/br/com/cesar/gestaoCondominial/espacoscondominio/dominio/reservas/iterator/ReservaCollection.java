package br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.iterator;

import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.Reserva;

import java.util.List;
import java.util.Objects;

public class ReservaCollection {

    private final List<Reserva> reservas;

    public ReservaCollection(List<Reserva> reservas) {
        this.reservas = Objects.requireNonNull(reservas);
    }

    public ReservaIterator iterator() {
        return new ReservaListIterator(reservas);
    }
}
