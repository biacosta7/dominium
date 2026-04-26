package com.dominium.backend.domain.assembleia.repository;

import com.dominium.backend.domain.assembleia.Assembleia;
import com.dominium.backend.domain.assembleia.AssembleiaId;

import java.util.List;
import java.util.Optional;

public interface AssembleiaRepository {
    Assembleia save(Assembleia assembleia);

    Optional<Assembleia> findById(AssembleiaId id);

    List<Assembleia> findAll();
}
