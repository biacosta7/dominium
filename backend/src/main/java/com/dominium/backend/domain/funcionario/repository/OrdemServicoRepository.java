package com.dominium.backend.domain.funcionario.repository;

import com.dominium.backend.domain.funcionario.FuncionarioId;
import com.dominium.backend.domain.funcionario.OrdemServico;
import com.dominium.backend.domain.funcionario.OrdemServicoId;
import com.dominium.backend.domain.funcionario.StatusOrdemServico;

import java.util.List;
import java.util.Optional;

public interface OrdemServicoRepository {
    OrdemServico save(OrdemServico ordemServico);
    Optional<OrdemServico> findById(OrdemServicoId id);
    List<OrdemServico> findByFuncionarioId(FuncionarioId funcionarioId);
    boolean existeAtivaParaFuncionario(FuncionarioId funcionarioId);
}
