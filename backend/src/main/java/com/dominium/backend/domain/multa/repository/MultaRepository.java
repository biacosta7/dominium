package com.dominium.backend.domain.multa.repository;

import com.dominium.backend.domain.multa.Multa;
import com.dominium.backend.domain.multa.MultaId;
import com.dominium.backend.domain.multa.StatusMulta;
import com.dominium.backend.domain.unidade.UnidadeId;

import java.util.List;
import java.util.Optional;

public interface MultaRepository {

    Multa save(Multa multa);

    Optional<Multa> findById(MultaId id);

    List<Multa> findAll();

    List<Multa> findByUnidadeId(UnidadeId unidadeId);

    List<Multa> findByUnidadeIdAndStatus(UnidadeId unidadeId, StatusMulta status);

    List<Multa> findByOcorrenciaId(Long ocorrenciaId);

    long countByUnidadeIdAndDescricao(UnidadeId unidadeId, String descricao);

    void deleteById(MultaId id);
}