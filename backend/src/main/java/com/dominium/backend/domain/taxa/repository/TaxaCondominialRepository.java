package com.dominium.backend.domain.taxa.repository;

import com.dominium.backend.domain.taxa.TaxaCondominial;
import com.dominium.backend.domain.taxa.TaxaId;
import com.dominium.backend.domain.unidade.UnidadeId;

import java.util.List;
import java.util.Optional;

public interface TaxaCondominialRepository {
    void salvar(TaxaCondominial taxa);
    void atualizar(TaxaCondominial taxa);
    Optional<TaxaCondominial> buscarPorId(TaxaId id);
    List<TaxaCondominial> listarPorUnidade(UnidadeId unidadeId);
    List<TaxaCondominial> listarTodas();
    boolean existeTaxaAtrasadaPorUnidade(UnidadeId unidadeId);
}