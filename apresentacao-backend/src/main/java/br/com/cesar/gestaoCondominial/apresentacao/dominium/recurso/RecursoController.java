package br.com.cesar.gestaoCondominial.apresentacao.dominium.recurso;

import br.com.cesar.gestaoCondominial.financeiro.aplicacao.recurso.dto.AbrirRecursoRequestDTO;
import br.com.cesar.gestaoCondominial.financeiro.aplicacao.recurso.dto.JulgarRecursoRequestDTO;
import br.com.cesar.gestaoCondominial.financeiro.aplicacao.recurso.usecase.AbrirRecursoUseCase;
import br.com.cesar.gestaoCondominial.financeiro.aplicacao.recurso.usecase.JulgarRecursoUseCase;
import br.com.cesar.gestaoCondominial.financeiro.aplicacao.recurso.usecase.ListarRecursosUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/recursos")
@RequiredArgsConstructor
public class RecursoController {

    private final AbrirRecursoUseCase abrirRecursoUseCase;
    private final JulgarRecursoUseCase julgarRecursoUseCase;
    private final ListarRecursosUseCase listarRecursosUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UUID abrirRecurso(@RequestBody AbrirRecursoRequestDTO request) {
        return abrirRecursoUseCase.execute(request);
    }

    @GetMapping
    public List<ListarRecursosUseCase.Response> listarRecursos() {
        return listarRecursosUseCase.execute();
    }

    @PutMapping("/{id}/julgar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void julgarRecurso(@PathVariable UUID id, @RequestBody JulgarRecursoRequestDTO request) {
        julgarRecursoUseCase.execute(id, request);
    }
}
