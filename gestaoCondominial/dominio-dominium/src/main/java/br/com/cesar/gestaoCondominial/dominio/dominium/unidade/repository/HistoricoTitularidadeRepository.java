package br.com.cesar.gestaoCondominial.dominio.dominium.unidade.repository;

import java.util.List;

import br.com.cesar.gestaoCondominial.dominio.dominium.unidade.HistoricoTitularidade;
import br.com.cesar.gestaoCondominial.dominio.dominium.unidade.UnidadeId;


public interface HistoricoTitularidadeRepository {

    HistoricoTitularidade save(HistoricoTitularidade historico);

    List<HistoricoTitularidade> findByUnidadeId(UnidadeId unidadeId);
}