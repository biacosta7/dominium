package br.com.cesar.gestaoCondominial.moradores.dominio.unidade.repository;

import java.util.List;

import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.HistoricoTitularidade;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.UnidadeId;


public interface HistoricoTitularidadeRepository {

    HistoricoTitularidade save(HistoricoTitularidade historico);

    List<HistoricoTitularidade> findByUnidadeId(UnidadeId unidadeId);
}