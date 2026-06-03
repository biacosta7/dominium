package br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.iterator;

import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.Reserva;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.repository.ReservaRepository;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.UsuarioId;

import java.util.List;
import java.util.NoSuchElementException;

public class ReservaDatabaseIterator implements ReservaIterator {
    private final ReservaRepository repository;
    private final UsuarioId usuarioId;
    private final int pageSize;
    private int offset;
    private List<Reserva> currentPage;
    private int indexInPage;

    public ReservaDatabaseIterator(ReservaRepository repository, UsuarioId usuarioId) {
        this(repository, usuarioId, 5); // Default page size of 5
    }

    public ReservaDatabaseIterator(ReservaRepository repository, UsuarioId usuarioId, int pageSize) {
        this.repository = repository;
        this.usuarioId = usuarioId;
        this.pageSize = pageSize;
        this.offset = 0;
        this.indexInPage = 0;
        fetchNextPage();
    }

    private void fetchNextPage() {
        this.currentPage = repository.buscarPorUsuarioPaginado(usuarioId, pageSize, offset);
        this.offset += pageSize;
        this.indexInPage = 0;
    }

    @Override
    public boolean hasNext() {
        if (indexInPage < currentPage.size()) {
            return true;
        }
        if (currentPage.size() < pageSize) {
            return false;
        }
        fetchNextPage();
        return !currentPage.isEmpty();
    }

    @Override
    public Reserva next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return currentPage.get(indexInPage++);
    }
}
