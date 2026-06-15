package br.com.cesar.gestaoCondominial.governanca.dominio.governanca.voto.iterator;

import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.pauta.PautaId;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.voto.Voto;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.voto.VotoRepository;

import java.util.List;
import java.util.Objects;

public class VotoCollection {

    private final List<Voto> votos;
    private final VotoRepository repository;
    private final PautaId pautaId;

    public VotoCollection(List<Voto> votos) {
        this.votos = Objects.requireNonNull(votos);
        this.repository = null;
        this.pautaId = null;
    }

    public VotoCollection(VotoRepository repository, PautaId pautaId) {
        this.repository = Objects.requireNonNull(repository);
        this.pautaId = Objects.requireNonNull(pautaId);
        this.votos = null;
    }

    public VotoIterator iterator() {
        if (repository != null) {
            return new VotoDatabaseIterator(repository, pautaId);
        }
        return new VotoListIterator(votos);
    }
}
