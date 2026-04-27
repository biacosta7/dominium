package com.dominium.backend.presentation.funcionario;

import com.dominium.backend.application.funcionario.dto.CriarOrdemServicoRequest;
import com.dominium.backend.application.funcionario.dto.OrdemServicoResponse;
import com.dominium.backend.application.funcionario.usecase.CriarOrdemServicoUseCase;
import com.dominium.backend.application.funcionario.usecase.EncerrarOrdemServicoUseCase;
import com.dominium.backend.domain.funcionario.OrdemServico;
import com.dominium.backend.domain.shared.exceptions.ExceptionHandler;
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
