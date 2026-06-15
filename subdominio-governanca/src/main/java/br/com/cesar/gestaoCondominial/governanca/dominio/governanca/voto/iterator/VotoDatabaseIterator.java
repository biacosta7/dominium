package br.com.cesar.gestaoCondominial.governanca.dominio.governanca.voto.iterator;

import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.pauta.PautaId;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.voto.Voto;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.voto.VotoRepository;

import java.util.List;
import java.util.NoSuchElementException;

public class VotoDatabaseIterator implements VotoIterator {
    private final VotoRepository repository;
    private final PautaId pautaId;
    private final int pageSize;
    private int offset;
    private List<Voto> currentPage;
    private int indexInPage;

    public VotoDatabaseIterator(VotoRepository repository, PautaId pautaId) {
        this(repository, pautaId, 5); // Default page size of 5
    }

    public VotoDatabaseIterator(VotoRepository repository, PautaId pautaId, int pageSize) {
        this.repository = repository;
        this.pautaId = pautaId;
        this.pageSize = pageSize;
        this.offset = 0;
        this.indexInPage = 0;
        fetchNextPage();
    }

    private void fetchNextPage() {
        this.currentPage = repository.buscarPorPautaPaginado(pautaId, pageSize, offset);
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
    public Voto next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return currentPage.get(indexInPage++);
    }
}
