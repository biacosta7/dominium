package br.com.cesar.gestaoCondominial.dominio.dominium.recurso.repository;


import br.com.cesar.gestaoCondominial.dominio.dominium.recurso.Recurso;
import br.com.cesar.gestaoCondominial.dominio.dominium.recurso.RecursoId;

import java.util.Optional;

public interface RecursoRepository {
    void salvar(Recurso recurso);
    void atualizar(Recurso recurso);
    Optional<Recurso> buscarPorId(RecursoId id);
}