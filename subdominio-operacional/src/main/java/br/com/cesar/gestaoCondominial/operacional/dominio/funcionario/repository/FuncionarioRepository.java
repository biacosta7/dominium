package br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.repository;

import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.Funcionario;
import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.FuncionarioId;
import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.StatusFuncionario;

import java.util.List;
import java.util.Optional;

public interface FuncionarioRepository {
    Funcionario save(Funcionario funcionario);
    Optional<Funcionario> findById(FuncionarioId id);
    List<Funcionario> findAll();
    List<Funcionario> findByStatus(StatusFuncionario status);
}
