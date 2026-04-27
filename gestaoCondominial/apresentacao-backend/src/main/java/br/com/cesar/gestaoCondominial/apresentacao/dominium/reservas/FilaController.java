package com.dominium.backend.presentation.reservas;

import com.dominium.backend.application.reservas.dto.FilaRequestDTO;
import com.dominium.backend.application.reservas.usecase.AdicionarNaFilaUseCase;
import com.dominium.backend.domain.areacomum.AreaComumId;
import com.dominium.backend.domain.reservas.FilaDeEspera;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fila")
public class FilaController {

    private final AdicionarNaFilaUseCase useCase;

    public FilaController(AdicionarNaFilaUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    public FilaDeEspera adicionar(@RequestBody FilaRequestDTO dto) {
        return useCase.executar(
                new AreaComumId(dto.getAreaComumId()),
                dto.getUsuarioId(),
                dto.getData(),
                dto.getHoraInicio(),
                dto.getHoraFim()
        );
    }
}