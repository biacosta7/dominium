package br.com.cesar.gestaoCondominial.dominio.dominium.funcionario.repository;

import br.com.cesar.gestaoCondominial.dominio.dominium.funcionario.Funcionario;
import br.com.cesar.gestaoCondominial.dominio.dominium.funcionario.FuncionarioId;
import br.com.cesar.gestaoCondominial.dominio.dominium.funcionario.StatusFuncionario;

import java.util.List;
import java.util.Optional;

public interface FuncionarioRepository {
    Funcionario save(Funcionario funcionario);
    Optional<Funcionario> findById(FuncionarioId id);
    List<Funcionario> findAll();
    List<Funcionario> findByStatus(StatusFuncionario status);
}
