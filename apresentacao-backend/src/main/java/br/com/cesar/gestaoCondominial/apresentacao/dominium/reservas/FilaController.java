package br.com.cesar.gestaoCondominial.apresentacao.dominium.reservas;

import br.com.cesar.gestaoCondominial.espacoscondominio.aplicacao.reservas.dto.FilaRequestDTO;
import br.com.cesar.gestaoCondominial.espacoscondominio.aplicacao.reservas.dto.FilaResponseDTO;
import br.com.cesar.gestaoCondominial.espacoscondominio.aplicacao.reservas.usecase.AdicionarNaFilaUseCase;
import br.com.cesar.gestaoCondominial.espacoscondominio.aplicacao.reservas.usecase.ListarFilaUseCase;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.areacomum.AreaComumId;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.FilaDeEspera;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.UsuarioId;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fila")
public class FilaController {

    private final AdicionarNaFilaUseCase adicionarNaFilaUseCase;
    private final ListarFilaUseCase listarFilaUseCase;

    public FilaController(AdicionarNaFilaUseCase adicionarNaFilaUseCase, ListarFilaUseCase listarFilaUseCase) {
        this.adicionarNaFilaUseCase = adicionarNaFilaUseCase;
        this.listarFilaUseCase = listarFilaUseCase;
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<FilaResponseDTO> listarPorUsuario(@PathVariable Long usuarioId) {
        return listarFilaUseCase.executar(new UsuarioId(usuarioId));
    }

    @PostMapping
    public FilaDeEspera adicionar(@RequestBody FilaRequestDTO dto) {
        return adicionarNaFilaUseCase.executar(
                new AreaComumId(dto.getAreaComumId()),
                new UsuarioId(dto.getUsuarioId()),
                dto.getData(),
                dto.getHoraInicio(),
                dto.getHoraFim()
        );
    }
}