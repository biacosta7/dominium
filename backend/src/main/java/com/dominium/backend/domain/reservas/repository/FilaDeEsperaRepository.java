package com.dominium.backend.domain.reservas.repository;

import com.dominium.backend.domain.reservas.FilaDeEspera;
import com.dominium.backend.domain.areacomum.AreaComumId;
import java.util.List;
import java.util.Optional;

public interface FilaDeEsperaRepository {
    FilaDeEspera salvar(FilaDeEspera fila);
    Optional<FilaDeEspera> buscarPorId(String id);
    List<FilaDeEspera> listarPorArea(AreaComumId areaId);
}