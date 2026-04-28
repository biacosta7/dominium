package br.com.cesar.gestaoCondominial.financeiro.dominio.taxa.repository;

import br.com.cesar.gestaoCondominial.financeiro.dominio.taxa.TaxaCondominial;
import br.com.cesar.gestaoCondominial.financeiro.dominio.taxa.TaxaId;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.UnidadeId;

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