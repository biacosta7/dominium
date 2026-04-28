package br.com.cesar.gestaoCondominial.apresentacao.dominium.financeiro;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.CategoriaDespesa;

import br.com.cesar.gestaoCondominial.financeiro.aplicacao.financeiro.dto.DespesaRequestDTO;
import br.com.cesar.gestaoCondominial.financeiro.aplicacao.financeiro.dto.DespesaResponseDTO;
import br.com.cesar.gestaoCondominial.financeiro.aplicacao.financeiro.dto.OrcamentoRequestDTO;
import br.com.cesar.gestaoCondominial.financeiro.aplicacao.financeiro.dto.OrcamentoResponseDTO;
import br.com.cesar.gestaoCondominial.financeiro.aplicacao.financeiro.usecase.AprovarDespesaExtraordinariaUseCase;
import br.com.cesar.gestaoCondominial.financeiro.aplicacao.financeiro.usecase.CadastrarOrcamentoUseCase;
import br.com.cesar.gestaoCondominial.financeiro.aplicacao.financeiro.usecase.ConsultarSaldoUseCase;
import br.com.cesar.gestaoCondominial.financeiro.aplicacao.financeiro.usecase.RegistrarDespesaUseCase;
import br.com.cesar.gestaoCondominial.financeiro.aplicacao.financeiro.usecase.GetOrcamentoPorAnoUseCase;
import br.com.cesar.gestaoCondominial.financeiro.aplicacao.financeiro.usecase.ListOrcamentosUseCase;
import br.com.cesar.gestaoCondominial.financeiro.aplicacao.financeiro.usecase.ListDespesasPorOrcamentoUseCase;
import br.com.cesar.gestaoCondominial.financeiro.aplicacao.financeiro.usecase.GetDespesaUseCase;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.Despesa;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.Orcamento;
import br.com.cesar.gestaoCondominial.apresentacao.dominium.exception.ExceptionHandler;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/financeiro")
public class FinanceiroController {

    private final CadastrarOrcamentoUseCase cadastrarOrcamentoUseCase;
    private final RegistrarDespesaUseCase registrarDespesaUseCase;
    private final AprovarDespesaExtraordinariaUseCase aprovarDespesaExtraordinariaUseCase;
    private final ConsultarSaldoUseCase consultarSaldoUseCase;
    private final GetOrcamentoPorAnoUseCase getOrcamentoPorAnoUseCase;
    private final ListOrcamentosUseCase listOrcamentosUseCase;
    private final ListDespesasPorOrcamentoUseCase listDespesasPorOrcamentoUseCase;
    private final GetDespesaUseCase getDespesaUseCase;
    private final ExceptionHandler exceptionHandler;

    public FinanceiroController(
            CadastrarOrcamentoUseCase cadastrarOrcamentoUseCase,
            RegistrarDespesaUseCase registrarDespesaUseCase,
            AprovarDespesaExtraordinariaUseCase aprovarDespesaExtraordinariaUseCase,
            ConsultarSaldoUseCase consultarSaldoUseCase,
            GetOrcamentoPorAnoUseCase getOrcamentoPorAnoUseCase,
            ListOrcamentosUseCase listOrcamentosUseCase,
            ListDespesasPorOrcamentoUseCase listDespesasPorOrcamentoUseCase,
            GetDespesaUseCase getDespesaUseCase,
            ExceptionHandler exceptionHandler) {
        this.cadastrarOrcamentoUseCase = cadastrarOrcamentoUseCase;
        this.registrarDespesaUseCase = registrarDespesaUseCase;
        this.aprovarDespesaExtraordinariaUseCase = aprovarDespesaExtraordinariaUseCase;
        this.consultarSaldoUseCase = consultarSaldoUseCase;
        this.getOrcamentoPorAnoUseCase = getOrcamentoPorAnoUseCase;
        this.listOrcamentosUseCase = listOrcamentosUseCase;
        this.listDespesasPorOrcamentoUseCase = listDespesasPorOrcamentoUseCase;
        this.getDespesaUseCase = getDespesaUseCase;
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

    @GetMapping("/orcamentos")
    public ResponseEntity<?> listarOrcamentos() {
        return exceptionHandler.withHandler(() -> {
            List<OrcamentoResponseDTO> response = listOrcamentosUseCase.execute()
                    .stream()
                    .map(OrcamentoResponseDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        });
    }

    @GetMapping("/orcamentos/{ano}")
    public ResponseEntity<?> detalharOrcamento(@PathVariable Integer ano) {
        return exceptionHandler.withHandler(() -> {
            Orcamento orcamento = getOrcamentoPorAnoUseCase.execute(ano);
            return ResponseEntity.ok(new OrcamentoResponseDTO(orcamento));
        });
    }

    @GetMapping("/orcamentos/{ano}/despesas")
    public ResponseEntity<?> listarDespesasPorOrcamento(
            @PathVariable Integer ano,
            @RequestParam(required = false) CategoriaDespesa categoria) {
        return exceptionHandler.withHandler(() -> {
            List<DespesaResponseDTO> response = listDespesasPorOrcamentoUseCase.execute(ano, categoria)
                    .stream()
                    .map(DespesaResponseDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        });
    }

    @GetMapping("/despesas/{id}")
    public ResponseEntity<?> detalharDespesa(@PathVariable Long id) {
        return exceptionHandler.withHandler(() -> {
            Despesa despesa = getDespesaUseCase.execute(id);
            return ResponseEntity.ok(new DespesaResponseDTO(despesa));
        });
    }
}
