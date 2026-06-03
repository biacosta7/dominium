package br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.iterator;

import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.Reserva;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.repository.ReservaRepository;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.UsuarioId;

import java.util.List;
import java.util.Objects;

public class ReservaCollection {

    private final List<Reserva> reservas;
    private final ReservaRepository repository;
    private final UsuarioId usuarioId;

    public ReservaCollection(List<Reserva> reservas) {
        this.reservas = Objects.requireNonNull(reservas);
        this.repository = null;
        this.usuarioId = null;
    }

    public ReservaCollection(ReservaRepository repository, UsuarioId usuarioId) {
        this.repository = Objects.requireNonNull(repository);
        this.usuarioId = Objects.requireNonNull(usuarioId);
        this.reservas = null;
    }

    public ReservaIterator iterator() {
        if (repository != null) {
            return new ReservaDatabaseIterator(repository, usuarioId);
        }
        return new ReservaListIterator(reservas);
    }
}
