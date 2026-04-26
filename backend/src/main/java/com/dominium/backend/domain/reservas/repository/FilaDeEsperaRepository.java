package com.dominium.backend.domain.reservas.repository;

import com.dominium.backend.domain.reservas.FilaDeEspera;
import com.dominium.backend.domain.areacomum.AreaComumId;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.time.LocalTime;

public interface FilaDeEsperaRepository {
    FilaDeEspera salvar(FilaDeEspera fila);
    Optional<FilaDeEspera> buscarPorId(String id);
    List<FilaDeEspera> listarPorArea(AreaComumId areaId);
    Optional<FilaDeEspera> buscarProximoNaFila(AreaComumId areaId, LocalDate data, LocalTime inicio, LocalTime fim);
}