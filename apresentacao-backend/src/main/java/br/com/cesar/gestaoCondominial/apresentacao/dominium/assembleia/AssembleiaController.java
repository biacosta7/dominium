package br.com.cesar.gestaoCondominial.apresentacao.dominium.assembleia;

import br.com.cesar.gestaoCondominial.governanca.aplicacao.assembleia.dto.AssembleiaResponse;
import br.com.cesar.gestaoCondominial.governanca.aplicacao.assembleia.dto.CriarAssembleiaRequest;
import br.com.cesar.gestaoCondominial.governanca.aplicacao.assembleia.dto.EditarAssembleiaRequest;
import br.com.cesar.gestaoCondominial.governanca.aplicacao.assembleia.usecase.CancelarAssembleiaUseCase;
import br.com.cesar.gestaoCondominial.governanca.aplicacao.assembleia.usecase.CriarAssembleiaUseCase;
import br.com.cesar.gestaoCondominial.governanca.aplicacao.assembleia.usecase.EditarAssembleiaUseCase;
import br.com.cesar.gestaoCondominial.governanca.aplicacao.assembleia.usecase.EncerrarAssembleiaUseCase;
import br.com.cesar.gestaoCondominial.governanca.dominio.assembleia.Assembleia;
import br.com.cesar.gestaoCondominial.apresentacao.dominium.exception.ExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/assembleias")
public class AssembleiaController {

    private final CriarAssembleiaUseCase criarUseCase;
    private final EditarAssembleiaUseCase editarUseCase;
    private final CancelarAssembleiaUseCase cancelarUseCase;
    private final EncerrarAssembleiaUseCase encerrarUseCase;
    private final ExceptionHandler exceptionHandler;

    public AssembleiaController(
            CriarAssembleiaUseCase criarUseCase,
            EditarAssembleiaUseCase editarUseCase,
            CancelarAssembleiaUseCase cancelarUseCase,
            EncerrarAssembleiaUseCase encerrarUseCase,
            ExceptionHandler exceptionHandler
    ) {
        this.criarUseCase = criarUseCase;
        this.editarUseCase = editarUseCase;
        this.cancelarUseCase = cancelarUseCase;
        this.encerrarUseCase = encerrarUseCase;
        this.exceptionHandler = exceptionHandler;
    }

    @PostMapping
    public ResponseEntity<?> criar(
            @RequestHeader("X-Sindico-Id") Long sindicoId,
            @RequestBody CriarAssembleiaRequest request
    ) {
        return exceptionHandler.withHandler(() -> {
            Assembleia assembleia = criarUseCase.executar(
                    sindicoId,
                    request.titulo(),
                    request.dataHora(),
                    request.local(),
                    request.pauta()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(AssembleiaResponse.from(assembleia));
        });
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(
            @RequestHeader("X-Sindico-Id") Long sindicoId,
            @PathVariable Long id,
            @RequestBody EditarAssembleiaRequest request
    ) {
        return exceptionHandler.withHandler(() -> {
            Assembleia assembleia = editarUseCase.executar(
                    sindicoId,
                    id,
                    request.titulo(),
                    request.dataHora(),
                    request.local(),
                    request.pauta()
            );
            return ResponseEntity.ok(AssembleiaResponse.from(assembleia));
        });
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelar(
            @RequestHeader("X-Sindico-Id") Long sindicoId,
            @PathVariable Long id
    ) {
        return exceptionHandler.withHandler(() -> {
            Assembleia assembleia = cancelarUseCase.executar(sindicoId, id);
            return ResponseEntity.ok(AssembleiaResponse.from(assembleia));
        });
    }

    @PutMapping("/{id}/encerrar")
    public ResponseEntity<?> encerrar(
            @RequestHeader("X-Sindico-Id") Long sindicoId,
            @PathVariable Long id
    ) {
        return exceptionHandler.withHandler(() -> {
            Assembleia assembleia = encerrarUseCase.executar(sindicoId, id);
            return ResponseEntity.ok(AssembleiaResponse.from(assembleia));
        });
    }
}
