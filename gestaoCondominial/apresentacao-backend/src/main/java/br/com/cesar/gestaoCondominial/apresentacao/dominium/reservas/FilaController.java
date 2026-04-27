package br.com.cesar.gestaoCondominial.apresentacao.dominium.reservas;

import br.com.cesar.gestaoCondominial.aplicacao.dominium.reservas.dto.FilaRequestDTO;
import br.com.cesar.gestaoCondominial.aplicacao.dominium.reservas.usecase.AdicionarNaFilaUseCase;
import br.com.cesar.gestaoCondominial.dominio.dominium.areacomum.AreaComumId;
import br.com.cesar.gestaoCondominial.dominio.dominium.reservas.FilaDeEspera;
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