package br.com.cesar.gestaoCondominial.espacoscondominio.dominio.areacomum;

import java.util.Optional;

public interface AreaComumRepository {

    Optional<AreaComum> findById(AreaComumId id);

    AreaComum save(AreaComum area);
}
