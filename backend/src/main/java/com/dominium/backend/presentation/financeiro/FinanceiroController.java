package com.dominium.backend.presentation.financeiro;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dominium.backend.application.financeiro.dto.DespesaRequestDTO;
import com.dominium.backend.application.financeiro.dto.DespesaResponseDTO;
import com.dominium.backend.application.financeiro.dto.OrcamentoRequestDTO;
import com.dominium.backend.application.financeiro.dto.OrcamentoResponseDTO;
import com.dominium.backend.application.financeiro.usecase.AprovarDespesaExtraordinariaUseCase;
import com.dominium.backend.application.financeiro.usecase.CadastrarOrcamentoUseCase;
import com.dominium.backend.application.financeiro.usecase.ConsultarSaldoUseCase;
import com.dominium.backend.application.financeiro.usecase.RegistrarDespesaUseCase;
import com.dominium.backend.domain.financeiro.Despesa;
import com.dominium.backend.domain.financeiro.Orcamento;
import com.dominium.backend.domain.shared.exceptions.ExceptionHandler;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/financeiro")
public class FinanceiroController {

    private final CadastrarOrcamentoUseCase cadastrarOrcamentoUseCase;
    private final RegistrarDespesaUseCase registrarDespesaUseCase;
    private final AprovarDespesaExtraordinariaUseCase aprovarDespesaExtraordinariaUseCase;
    private final ConsultarSaldoUseCase consultarSaldoUseCase;
    private final ExceptionHandler exceptionHandler;

    public FinanceiroController(
            CadastrarOrcamentoUseCase cadastrarOrcamentoUseCase,
            RegistrarDespesaUseCase registrarDespesaUseCase,
            AprovarDespesaExtraordinariaUseCase aprovarDespesaExtraordinariaUseCase,
            ConsultarSaldoUseCase consultarSaldoUseCase,
            ExceptionHandler exceptionHandler) {
        this.cadastrarOrcamentoUseCase = cadastrarOrcamentoUseCase;
        this.registrarDespesaUseCase = registrarDespesaUseCase;
        this.aprovarDespesaExtraordinariaUseCase = aprovarDespesaExtraordinariaUseCase;
        this.consultarSaldoUseCase = consultarSaldoUseCase;
        this.exceptionHandler = exceptionHandler;
    }

    @PostMapping("/orcamentos")
    public ResponseEntity<?> cadastrarOrcamento(@Valid @RequestBody OrcamentoRequestDTO request) {
        return exceptionHandler.withHandler(() -> {
            Orcamento orcamento = cadastrarOrcamentoUseCase.execute(request.getAno(), request.getValorTotal());
            return ResponseEntity.status(HttpStatus.CREATED).body(new OrcamentoResponseDTO(orcamento));
        });
    }

    @PostMapping("/despesas")
    public ResponseEntity<?> registrarDespesa(@Valid @RequestBody DespesaRequestDTO request) {
        return exceptionHandler.withHandler(() -> {
            Despesa despesa = registrarDespesaUseCase.execute(
                    request.getDescricao(),
                    request.getValor(),
                    request.getData(),
                    request.getCategoria(),
                    request.getTipo());
            return ResponseEntity.status(HttpStatus.CREATED).body(new DespesaResponseDTO(despesa));
        });
    }

    @PutMapping("/despesas/{id}/aprovar")
    public ResponseEntity<?> aprovarDespesa(@PathVariable Long id) {
        return exceptionHandler.withHandler(() -> {
            Despesa despesa = aprovarDespesaExtraordinariaUseCase.execute(id);
            return ResponseEntity.ok(new DespesaResponseDTO(despesa));
        });
    }

    @GetMapping("/orcamentos/{ano}/saldo")
    public ResponseEntity<?> consultarSaldo(@PathVariable Integer ano) {
        return exceptionHandler.withHandler(() -> {
            BigDecimal saldo = consultarSaldoUseCase.execute(ano);
            return ResponseEntity.ok(saldo);
        });
    }
}
