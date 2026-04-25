package com.dominium.backend.domain.assembleia.repository;

import com.dominium.backend.domain.assembleia.Assembleia;
import com.dominium.backend.domain.assembleia.AssembleiaId;
import com.dominium.backend.domain.assembleia.Pauta;

import java.util.Optional;

public interface AssembleiaRepository {
    void salvar(Assembleia assembleia);
    Optional<Assembleia> buscarPorId(AssembleiaId id);
    void registrarVoto(Pauta pauta, String unidadeId, String tipoVoto);
}