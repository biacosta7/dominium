package com.dominium.backend.presentation.funcionario;

import com.dominium.backend.domain.funcionario.Funcionario;
import br.com.cesar.gestaoCondominial.apresentacao.dominium.exception.ExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/funcionarios")
public class FuncionarioController {

    private final CadastrarFuncionarioUseCase cadastrarUseCase;
    private final RenovarContratoUseCase renovarContratoUseCase;
    private final RegistrarAvaliacaoUseCase registrarAvaliacaoUseCase;
    private final ExceptionHandler exceptionHandler;

    public FuncionarioController(
            CadastrarFuncionarioUseCase cadastrarUseCase,
            RenovarContratoUseCase renovarContratoUseCase,
            RegistrarAvaliacaoUseCase registrarAvaliacaoUseCase,
            ExceptionHandler exceptionHandler
    ) {
        this.cadastrarUseCase = cadastrarUseCase;
        this.renovarContratoUseCase = renovarContratoUseCase;
        this.registrarAvaliacaoUseCase = registrarAvaliacaoUseCase;
        this.exceptionHandler = exceptionHandler;
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(
            @RequestHeader("X-Sindico-Id") Long sindicoId,
            @RequestBody CadastrarFuncionarioRequest request
    ) {
        return exceptionHandler.withHandler(() -> {
            Funcionario f = cadastrarUseCase.executar(
                    sindicoId,
                    request.nome(), request.cpf(), request.email(), request.telefone(),
                    request.tipoVinculo(), request.contratoInicio(), request.contratoFim(),
                    request.valorMensal()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(FuncionarioResponse.from(f));
        });
    }

    @PutMapping("/{id}/contrato")
    public ResponseEntity<?> renovarContrato(
            @RequestHeader("X-Sindico-Id") Long sindicoId,
            @PathVariable String id,
            @RequestBody RenovarContratoRequest request
    ) {
        return exceptionHandler.withHandler(() -> {
            Funcionario f = renovarContratoUseCase.executar(sindicoId, id, request.novaDataFim(), request.novoValorMensal());
            return ResponseEntity.ok(FuncionarioResponse.from(f));
        });
    }

    @PostMapping("/{id}/avaliacoes")
    public ResponseEntity<?> registrarAvaliacao(
            @RequestHeader("X-Sindico-Id") Long sindicoId,
            @PathVariable String id,
            @RequestBody RegistrarAvaliacaoRequest request
    ) {
        return exceptionHandler.withHandler(() -> {
            registrarAvaliacaoUseCase.executar(sindicoId, id, request.positiva(), request.comentario());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        });
    }
}
