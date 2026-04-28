package br.com.cesar.gestaoCondominial.apresentacao.dominium.funcionario;

import br.com.cesar.gestaoCondominial.operacional.aplicacao.funcionario.dto.CriarOrdemServicoRequest;
import br.com.cesar.gestaoCondominial.operacional.aplicacao.funcionario.dto.OrdemServicoResponse;
import br.com.cesar.gestaoCondominial.operacional.aplicacao.funcionario.usecase.CriarOrdemServicoUseCase;
import br.com.cesar.gestaoCondominial.operacional.aplicacao.funcionario.usecase.EncerrarOrdemServicoUseCase;
import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.OrdemServico;
import br.com.cesar.gestaoCondominial.apresentacao.dominium.exception.ExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ordens-servico")
public class OrdemServicoController {

    private final CriarOrdemServicoUseCase criarUseCase;
    private final EncerrarOrdemServicoUseCase encerrarUseCase;
    private final ExceptionHandler exceptionHandler;

    public OrdemServicoController(
            CriarOrdemServicoUseCase criarUseCase,
            EncerrarOrdemServicoUseCase encerrarUseCase,
            ExceptionHandler exceptionHandler
    ) {
        this.criarUseCase = criarUseCase;
        this.encerrarUseCase = encerrarUseCase;
        this.exceptionHandler = exceptionHandler;
    }

    @PostMapping
    public ResponseEntity<?> criar(
            @RequestHeader("X-Sindico-Id") Long sindicoId,
            @RequestBody CriarOrdemServicoRequest request
    ) {
        return exceptionHandler.withHandler(() -> {
            OrdemServico os = criarUseCase.executar(
                    sindicoId,
                    request.funcionarioId(), request.descricao(),
                    request.dataInicio(), request.dataFim()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(OrdemServicoResponse.from(os));
        });
    }

    @PutMapping("/{id}/encerrar")
    public ResponseEntity<?> encerrar(
            @RequestHeader("X-Sindico-Id") Long sindicoId,
            @PathVariable String id
    ) {
        return exceptionHandler.withHandler(() -> {
            OrdemServico os = encerrarUseCase.executar(sindicoId, id);
            return ResponseEntity.ok(OrdemServicoResponse.from(os));
        });
    }
}
