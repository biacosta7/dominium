package br.com.cesar.gestaoCondominial.apresentacao.dominium.notificacao;

import br.com.cesar.gestaoCondominial.comunicacao.aplicacao.notificacao.dto.EnviarNotificacaoRequest;
import br.com.cesar.gestaoCondominial.comunicacao.aplicacao.notificacao.dto.NotificacaoResponse;
import br.com.cesar.gestaoCondominial.comunicacao.aplicacao.notificacao.usecase.EnviarNotificacaoUseCase;
import br.com.cesar.gestaoCondominial.comunicacao.aplicacao.notificacao.usecase.ListarNotificacoesUseCase;
import br.com.cesar.gestaoCondominial.comunicacao.aplicacao.notificacao.usecase.ListarTodasNotificacoesUseCase;
import br.com.cesar.gestaoCondominial.comunicacao.aplicacao.notificacao.usecase.MarcarComoLidaUseCase;
import br.com.cesar.gestaoCondominial.apresentacao.dominium.exception.ExceptionHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notificacoes")
public class NotificacaoController {

    private final ListarNotificacoesUseCase listarUseCase;
    private final ListarTodasNotificacoesUseCase listarTodasUseCase;
    private final MarcarComoLidaUseCase marcarComoLidaUseCase;
    private final EnviarNotificacaoUseCase enviarUseCase;
    private final ExceptionHandler exceptionHandler;

    public NotificacaoController(
            ListarNotificacoesUseCase listarUseCase,
            ListarTodasNotificacoesUseCase listarTodasUseCase,
            MarcarComoLidaUseCase marcarComoLidaUseCase,
            EnviarNotificacaoUseCase enviarUseCase,
            ExceptionHandler exceptionHandler
    ) {
        this.listarUseCase = listarUseCase;
        this.listarTodasUseCase = listarTodasUseCase;
        this.marcarComoLidaUseCase = marcarComoLidaUseCase;
        this.enviarUseCase = enviarUseCase;
        this.exceptionHandler = exceptionHandler;
    }

    @GetMapping
    public ResponseEntity<?> listarTodas() {
        return exceptionHandler.withHandler(() -> {
            List<NotificacaoResponse> response = listarTodasUseCase.executar()
                    .stream()
                    .map(NotificacaoResponse::from)
                    .toList();
            return ResponseEntity.ok(response);
        });
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> listar(@PathVariable Long usuarioId) {
        return exceptionHandler.withHandler(() -> {
            List<NotificacaoResponse> response = listarUseCase.executar(usuarioId)
                    .stream()
                    .map(NotificacaoResponse::from)
                    .toList();
            return ResponseEntity.ok(response);
        });
    }

    @PostMapping("/enviar")
    public ResponseEntity<?> enviar(
            @RequestHeader("X-Sindico-Id") Long sindicoId,
            @RequestBody EnviarNotificacaoRequest request
    ) {
        return exceptionHandler.withHandler(() -> {
            enviarUseCase.executar(sindicoId, request.mensagem(), request.tipo(), request.publico());
            return ResponseEntity.ok().build();
        });
    }

    @PatchMapping("/{id}/lida")
    public ResponseEntity<?> marcarComoLida(
            @PathVariable Long id,
            @RequestHeader("X-Usuario-Id") Long usuarioId
    ) {
        return exceptionHandler.withHandler(() -> {
            NotificacaoResponse response = NotificacaoResponse.from(
                    marcarComoLidaUseCase.executar(id, usuarioId)
            );
            return ResponseEntity.ok(response);
        });
    }
}
