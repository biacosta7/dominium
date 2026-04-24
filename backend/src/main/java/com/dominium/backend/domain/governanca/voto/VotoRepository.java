package com.dominium.backend.domain.governanca.voto;

import com.dominium.backend.domain.governanca.pauta.PautaId;

import java.util.Optional;

public interface VotoRepository {

    Voto save(Voto voto);

    Optional<Voto> findById(VotoId votoId);

    Optional<Voto> findByPauta(PautaId pautaId);
}
