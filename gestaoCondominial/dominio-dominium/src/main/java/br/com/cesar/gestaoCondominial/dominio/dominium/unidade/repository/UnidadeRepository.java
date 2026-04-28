package br.com.cesar.gestaoCondominial.dominio.dominium.unidade.repository;

import java.util.List;
import java.util.Optional;

import br.com.cesar.gestaoCondominial.dominio.dominium.unidade.Unidade;
import br.com.cesar.gestaoCondominial.dominio.dominium.unidade.UnidadeId;

public interface UnidadeRepository {
    Unidade save(Unidade unidade);
    Optional<Unidade> findById(UnidadeId id);
    Optional<Unidade> findByNumeroAndBloco(String numero, String bloco);
    List<Unidade> findAll();
    void deleteById(UnidadeId id);
}