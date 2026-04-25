package com.dominium.backend.domain.recurso.repository;

import com.dominium.backend.domain.recurso.Recurso;
import com.dominium.backend.domain.recurso.RecursoId;
import java.util.Optional;

public interface RecursoRepository {
    void salvar(Recurso recurso);
    void atualizar(Recurso recurso);
    Optional<Recurso> buscarPorId(RecursoId id);
}