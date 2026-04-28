package br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.repository;

import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.Orcamento;
import java.util.Optional;
import java.util.List;

public interface OrcamentoRepository {
    Orcamento save(Orcamento orcamento);
    Optional<Orcamento> findById(Long id);
    Optional<Orcamento> findByAno(Integer ano);
    List<Orcamento> findAll();
}
