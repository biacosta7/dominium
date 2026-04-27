package com.dominium.backend.domain.funcionario.repository;

import com.dominium.backend.domain.funcionario.AvaliacaoFuncionario;
import com.dominium.backend.domain.funcionario.FuncionarioId;

import java.util.List;

public interface AvaliacaoFuncionarioRepository {
    AvaliacaoFuncionario save(AvaliacaoFuncionario avaliacao);
    List<AvaliacaoFuncionario> findByFuncionarioId(FuncionarioId funcionarioId);
    long contarNegativasRecentes(FuncionarioId funcionarioId, int limite);
}
