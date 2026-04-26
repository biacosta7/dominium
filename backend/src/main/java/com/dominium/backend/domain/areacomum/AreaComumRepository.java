package com.dominium.backend.domain.areacomum;

import java.util.Optional;

public interface AreaComumRepository {

    Optional<AreaComum> findById(AreaComumId id);

    AreaComum save(AreaComum areaComum);
}
