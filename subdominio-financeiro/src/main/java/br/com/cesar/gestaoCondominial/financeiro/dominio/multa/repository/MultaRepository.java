package br.com.cesar.gestaoCondominial.financeiro.dominio.multa.repository;

import br.com.cesar.gestaoCondominial.financeiro.dominio.multa.Multa;
import br.com.cesar.gestaoCondominial.financeiro.dominio.multa.MultaId;
import br.com.cesar.gestaoCondominial.financeiro.dominio.multa.StatusMulta;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.UnidadeId;

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