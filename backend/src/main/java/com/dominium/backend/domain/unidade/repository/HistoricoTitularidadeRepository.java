package com.dominium.backend.domain.unidade.repository;

import java.util.List;

import com.dominium.backend.domain.unidade.HistoricoTitularidade;
import com.dominium.backend.domain.unidade.UnidadeId;

public interface HistoricoTitularidadeRepository {

    HistoricoTitularidade save(HistoricoTitularidade historico);

    List<HistoricoTitularidade> findByUnidadeId(UnidadeId unidadeId);
}