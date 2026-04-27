package br.com.cesar.gestaoCondominial.dominio.dominium.funcionario.repository;

import br.com.cesar.gestaoCondominial.dominio.dominium.funcionario.OrdemServico;
import br.com.cesar.gestaoCondominial.dominio.dominium.funcionario.OrdemServicoId;
import br.com.cesar.gestaoCondominial.dominio.dominium.funcionario.FuncionarioId;

import java.util.List;
import java.util.Optional;

public interface OrdemServicoRepository {
    OrdemServico save(OrdemServico ordemServico);
    Optional<OrdemServico> findById(OrdemServicoId id);
    List<OrdemServico> findByFuncionarioId(FuncionarioId funcionarioId);
    boolean existeAtivaParaFuncionario(FuncionarioId funcionarioId);
}
