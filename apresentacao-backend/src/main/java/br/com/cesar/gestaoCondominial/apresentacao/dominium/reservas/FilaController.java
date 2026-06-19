package br.com.cesar.gestaoCondominial.apresentacao.dominium.reservas;

import br.com.cesar.gestaoCondominial.espacoscondominio.aplicacao.reservas.dto.FilaRequestDTO;
import br.com.cesar.gestaoCondominial.espacoscondominio.aplicacao.reservas.dto.FilaResponseDTO;
import br.com.cesar.gestaoCondominial.espacoscondominio.aplicacao.reservas.usecase.AdicionarNaFilaUseCase;
import br.com.cesar.gestaoCondominial.espacoscondominio.aplicacao.reservas.usecase.ListarFilaUseCase;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.areacomum.AreaComumId;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.FilaDeEspera;
import br.com.cesar.gestaoCondominial.apresentacao.dominium.exception.ExceptionHandler;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.UsuarioId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fila")
public class FilaController {

    private final AdicionarNaFilaUseCase adicionarNaFilaUseCase;
    private final ListarFilaUseCase listarFilaUseCase;
    private final ExceptionHandler exceptionHandler;

    public FilaController(
            AdicionarNaFilaUseCase adicionarNaFilaUseCase,
            ListarFilaUseCase listarFilaUseCase,
            ExceptionHandler exceptionHandler) {
        this.adicionarNaFilaUseCase = adicionarNaFilaUseCase;
        this.listarFilaUseCase = listarFilaUseCase;
        this.exceptionHandler = exceptionHandler;
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<FilaResponseDTO>> listarPorUsuario(@PathVariable Long usuarioId) {
        return exceptionHandler.withHandler(() -> {
            List<FilaResponseDTO> response = listarFilaUseCase.executar(new UsuarioId(usuarioId));
            return ResponseEntity.ok(response);
        });
    }

    @PostMapping
    public ResponseEntity<FilaDeEspera> adicionar(@RequestBody FilaRequestDTO dto) {
        return exceptionHandler.withHandler(() -> {
            FilaDeEspera fila = adicionarNaFilaUseCase.executar(
                    new AreaComumId(dto.getAreaComumId()),
                    new UsuarioId(dto.getUsuarioId()),
                    dto.getData(),
                    dto.getHoraInicio(),
                    dto.getHoraFim()
            );
            return ResponseEntity.ok(fila);
        });
    }
}