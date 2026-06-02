package br.com.cesar.gestaoCondominial.governanca.dominio.governanca.voto.iterator;

import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.voto.Voto;

import java.util.List;

public class VotoListIterator implements VotoIterator {

    private final List<Voto> votos;
    private int position;

    public VotoListIterator(List<Voto> votos) {
        this.votos = votos;
        this.position = 0;
    }

    @Override
    public boolean hasNext() {
        return position < votos.size();
    }

    @Override
    public Voto next() {
        return votos.get(position++);
    }
}
