package br.com.cesar.gestaoCondominial.dominio.dominium.funcionario.repository;

import br.com.cesar.gestaoCondominial.dominio.dominium.funcionario.AvaliacaoFuncionario;
import br.com.cesar.gestaoCondominial.dominio.dominium.funcionario.FuncionarioId;

import java.util.List;

public interface AvaliacaoFuncionarioRepository {
    AvaliacaoFuncionario save(AvaliacaoFuncionario avaliacao);
    List<AvaliacaoFuncionario> findByFuncionarioId(FuncionarioId funcionarioId);
    long contarNegativasRecentes(FuncionarioId funcionarioId, int limite);
}
