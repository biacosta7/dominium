package com.dominium.backend.domain.areacomum;

import com.dominium.backend.domain.areacomum.AreaComum;

import java.util.Optional;

public interface AreaComumRepository {

    Optional<AreaComum> findById(Long id);
}
