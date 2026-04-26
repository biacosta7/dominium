package com.dominium.backend.domain.ocorrencia.repository;


import com.dominium.backend.domain.ocorrencia.Ocorrencia;
import java.util.Optional;
import java.util.List;

public interface OcorrenciaRepository {
    Ocorrencia salvar(Ocorrencia ocorrencia);
    Optional<Ocorrencia> buscarPorId(Long id);
    List<Ocorrencia> listarTodas();
}