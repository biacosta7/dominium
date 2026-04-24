package com.dominium.backend.application.reservas.usecase;

import com.dominium.backend.domain.areacomum.AreaComum;
import com.dominium.backend.domain.areacomum.AreaComumId;
import com.dominium.backend.domain.areacomum.AreaComumService;
import com.dominium.backend.domain.reservas.FilaDeEspera;
import com.dominium.backend.domain.reservas.repository.FilaDeEsperaRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class AdicionarNaFilaUseCase {
    private final FilaDeEsperaRepository repository;
    private final AreaComumService areaComumService;

    public AdicionarNaFilaUseCase(FilaDeEsperaRepository repository, AreaComumService areaComumService) {
        this.repository = repository;
        this.areaComumService = areaComumService;
    }

    public FilaDeEspera executar(AreaComumId areaId, Long usuarioId, LocalDateTime dataDesejada) {

        AreaComum area = areaComumService.buscarArea(areaId);

        FilaDeEspera fila = FilaDeEspera.criar(areaId, usuarioId, dataDesejada);

        return repository.salvar(fila);
    }
}