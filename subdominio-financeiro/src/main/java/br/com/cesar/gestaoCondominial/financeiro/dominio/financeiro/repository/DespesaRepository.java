package br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.repository;

import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.Despesa;
import java.util.Optional;
import java.util.List;

public interface DespesaRepository {
    Despesa save(Despesa despesa);
    Optional<Despesa> findById(Long id);
    List<Despesa> findByOrcamentoId(Long orcamentoId);
    List<Despesa> findAll();
}
