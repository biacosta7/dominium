package br.com.cesar.gestaoCondominial.apresentacao.dominium.governanca.pauta;

import br.com.cesar.gestaoCondominial.governanca.aplicacao.governanca.pauta.dto.CriarPautaRequest;
import br.com.cesar.gestaoCondominial.governanca.aplicacao.governanca.pauta.dto.PautaResponse;
import br.com.cesar.gestaoCondominial.governanca.aplicacao.governanca.pauta.usecase.AbrirPautaUseCase;
import br.com.cesar.gestaoCondominial.governanca.aplicacao.governanca.pauta.usecase.EncerrarPautaUseCase;
import br.com.cesar.gestaoCondominial.governanca.aplicacao.governanca.pauta.usecase.ListarPautasUseCase;
import br.com.cesar.gestaoCondominial.governanca.dominio.assembleia.AssembleiaId;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.pauta.Pauta;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.pauta.PautaId;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pautas")
public class PautaController {

    private final AbrirPautaUseCase abrirPautaUseCase;
    private final EncerrarPautaUseCase encerrarPautaUseCase;
    private final ListarPautasUseCase listarPautasUseCase;

    public PautaController(AbrirPautaUseCase abrirPautaUseCase, EncerrarPautaUseCase encerrarPautaUseCase,
                           ListarPautasUseCase listarPautasUseCase) {
        this.abrirPautaUseCase = abrirPautaUseCase;
        this.encerrarPautaUseCase = encerrarPautaUseCase;
        this.listarPautasUseCase = listarPautasUseCase;
    }

    @PostMapping
    public ResponseEntity<PautaResponse> abrirPauta(@RequestBody @Valid CriarPautaRequest request) {
        Pauta pauta = abrirPautaUseCase.executar(
                new AssembleiaId(request.assembleiaId()),
                request.titulo(),
                request.descricao(),
                request.quorum(),
                request.maioria()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(pauta));
    }

    @PatchMapping("/{id}/encerrar")
    public ResponseEntity<PautaResponse> encerrarPauta(@PathVariable Long id) {
        Pauta pauta = encerrarPautaUseCase.executar(new PautaId(id));

        return ResponseEntity.ok(toResponse(pauta));
    }


    @GetMapping
    public ResponseEntity<List<PautaResponse>> listarPautas() {
        List<PautaResponse> pautas = listarPautasUseCase.executar()
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(pautas);
    }

    // Mapeamento domínio → resposta HTTP

    private PautaResponse toResponse(Pauta pauta) {
        return new PautaResponse(
                pauta.getId().getValor(),
                pauta.getAssembleiaId().getValor(),
                pauta.getTitulo(),
                pauta.getDescricao(),
                pauta.getStatus().name(),
                pauta.getResultadoFinal() != null ? pauta.getResultadoFinal().name() : null
        );
    }
}