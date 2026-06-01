package br.com.cesar.gestaoCondominial.governanca.dominio.governanca.voto.iterator;

import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.voto.Voto;

import java.util.List;
import java.util.Objects;

public class VotoCollection {

    private final List<Voto> votos;

    public VotoCollection(List<Voto> votos) {
        this.votos = Objects.requireNonNull(votos);
    }

    public VotoIterator iterator() {
        return new VotoListIterator(votos);
    }
}
