package br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.iterator;

import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.Reserva;

public interface ReservaIterator {
    boolean hasNext();
    Reserva next();
}

