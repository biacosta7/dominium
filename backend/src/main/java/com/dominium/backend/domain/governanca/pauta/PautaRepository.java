package com.dominium.backend.domain.governanca.pauta;

import com.dominium.backend.domain.unidade.UnidadeId;

import java.util.List;
import java.util.Optional;

public interface PautaRepository {

    Pauta save(Pauta pauta);

    Optional<Pauta> findById(PautaId pautaId);

    List<Pauta> findAll(PautaId pautaId);

    void deleteById(PautaId pautaId);
}
