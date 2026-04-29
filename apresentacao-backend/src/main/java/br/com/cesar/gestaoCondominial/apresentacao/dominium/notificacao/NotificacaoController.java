package br.com.cesar.gestaoCondominial.apresentacao.dominium.notificacao;

import br.com.cesar.gestaoCondominial.comunicacao.aplicacao.notificacao.dto.NotificacaoResponse;
import br.com.cesar.gestaoCondominial.comunicacao.aplicacao.notificacao.usecase.ListarNotificacoesUseCase;
import br.com.cesar.gestaoCondominial.comunicacao.aplicacao.notificacao.usecase.MarcarComoLidaUseCase;
import br.com.cesar.gestaoCondominial.apresentacao.dominium.exception.ExceptionHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notificacoes")
public class NotificacaoController {

    private final ListarNotificacoesUseCase listarUseCase;
    private final MarcarComoLidaUseCase marcarComoLidaUseCase;
    private final ExceptionHandler exceptionHandler;

    public NotificacaoController(
            ListarNotificacoesUseCase listarUseCase,
            MarcarComoLidaUseCase marcarComoLidaUseCase,
            ExceptionHandler exceptionHandler
    ) {
        this.listarUseCase = listarUseCase;
        this.marcarComoLidaUseCase = marcarComoLidaUseCase;
        this.exceptionHandler = exceptionHandler;
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
