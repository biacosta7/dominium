package br.com.cesar.gestaoCondominial.governanca.dominio.governanca.voto.iterator;

import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.voto.Voto;

public interface VotoIterator {
    boolean hasNext();
    Voto next();
}
