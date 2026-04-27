package br.com.cesar.gestaoCondominial.dominio.dominium.areacomum;

import java.util.Optional;

public interface AreaComumRepository {

    Optional<AreaComum> findById(AreaComumId id);
}
