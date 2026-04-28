package br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.repository;

import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.OrdemServico;
import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.OrdemServicoId;
import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.FuncionarioId;

import java.util.List;
import java.util.Optional;

public interface OrdemServicoRepository {
    OrdemServico save(OrdemServico ordemServico);
    Optional<OrdemServico> findById(OrdemServicoId id);
    List<OrdemServico> findByFuncionarioId(FuncionarioId funcionarioId);
    boolean existeAtivaParaFuncionario(FuncionarioId funcionarioId);
}
