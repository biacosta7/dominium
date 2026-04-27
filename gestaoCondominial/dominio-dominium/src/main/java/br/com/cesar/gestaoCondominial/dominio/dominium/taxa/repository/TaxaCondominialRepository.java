package br.com.cesar.gestaoCondominial.dominio.dominium.taxa.repository;

import br.com.cesar.gestaoCondominial.dominio.dominium.taxa.TaxaCondominial;
import br.com.cesar.gestaoCondominial.dominio.dominium.taxa.TaxaId;
import br.com.cesar.gestaoCondominial.dominio.dominium.unidade.UnidadeId;

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