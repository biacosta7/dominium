package com.dominium.backend.domain.unidade.repository;

import java.util.List;
import java.util.Optional;

import com.dominium.backend.domain.unidade.Unidade;
import com.dominium.backend.domain.unidade.UnidadeId;

public interface UnidadeRepository {
    Unidade save(Unidade unidade);
    Optional<Unidade> findById(UnidadeId id);
    Optional<Unidade> findByNumeroAndBloco(String numero, String bloco);
    List<Unidade> findAll();
    void deleteById(UnidadeId id);
}