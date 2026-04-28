package br.com.cesar.gestaoCondominial.financeiro.dominio.recurso.repository;


import br.com.cesar.gestaoCondominial.financeiro.dominio.recurso.Recurso;
import br.com.cesar.gestaoCondominial.financeiro.dominio.recurso.RecursoId;

import java.util.Optional;

public interface RecursoRepository {
    void salvar(Recurso recurso);
    void atualizar(Recurso recurso);
    Optional<Recurso> buscarPorId(RecursoId id);
}