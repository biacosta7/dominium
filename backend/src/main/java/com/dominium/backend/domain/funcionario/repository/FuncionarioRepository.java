package com.dominium.backend.domain.funcionario.repository;

import com.dominium.backend.domain.funcionario.Funcionario;
import com.dominium.backend.domain.funcionario.FuncionarioId;
import com.dominium.backend.domain.funcionario.StatusFuncionario;

import java.util.List;
import java.util.Optional;

public interface FuncionarioRepository {
    Funcionario save(Funcionario funcionario);
    Optional<Funcionario> findById(FuncionarioId id);
    List<Funcionario> findAll();
    List<Funcionario> findByStatus(StatusFuncionario status);
}
