package br.com.cesar.gestaoCondominial.dominio.dominium.assembleia.repository;

import br.com.cesar.gestaoCondominial.dominio.dominium.assembleia.Assembleia;
import br.com.cesar.gestaoCondominial.dominio.dominium.assembleia.AssembleiaId;

import java.util.List;
import java.util.Optional;

public interface AssembleiaRepository {
    Assembleia save(Assembleia assembleia);
    Optional<Assembleia> findById(AssembleiaId id);
    List<Assembleia> findAll();
}
