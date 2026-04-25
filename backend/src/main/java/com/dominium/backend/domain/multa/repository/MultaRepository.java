package com.dominium.backend.domain.multa.repository;

import com.dominium.backend.domain.multa.Multa;
import com.dominium.backend.domain.multa.StatusMulta;

import java.util.List;
import java.util.Optional;

public interface MultaRepository {

    Multa save(Multa multa);

    Optional<Multa> findById(Long id);

    List<Multa> findAll();

    List<Multa> findByUnidadeId(Long unidadeId);

    List<Multa> findByUnidadeIdAndStatus(Long unidadeId, StatusMulta status);

    List<Multa> findByOcorrenciaId(Long ocorrenciaId);

    long countByUnidadeIdAndDescricao(Long unidadeId, String descricao);

    void deleteById(Long id);
}