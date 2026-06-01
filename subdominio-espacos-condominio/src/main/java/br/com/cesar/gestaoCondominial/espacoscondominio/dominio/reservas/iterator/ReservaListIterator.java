package br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.iterator;

import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.Reserva;

import java.util.List;

public class ReservaListIterator implements ReservaIterator {

    private final List<Reserva> reservas;
    private int position;

    public ReservaListIterator(List<Reserva> reservas) {
        this.reservas = reservas;
        this.position = 0;
    }

    @Override
    public boolean hasNext() {
        return position < reservas.size();
    }

    @Override
    public Reserva next() {
        return reservas.get(position++);
    }
}
