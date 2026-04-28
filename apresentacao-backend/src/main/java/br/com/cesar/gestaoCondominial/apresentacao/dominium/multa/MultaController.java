package br.com.cesar.gestaoCondominial.apresentacao.dominium.multa;

import br.com.cesar.gestaoCondominial.financeiro.aplicacao.multa.dto.ContestarMultaRequestDTO;
import br.com.cesar.gestaoCondominial.financeiro.aplicacao.multa.dto.CreateMultaRequestDTO;
import br.com.cesar.gestaoCondominial.financeiro.aplicacao.multa.dto.MultaResponseDTO;
import br.com.cesar.gestaoCondominial.financeiro.aplicacao.multa.dto.RegistrarPagamentoRequestDTO;
import br.com.cesar.gestaoCondominial.financeiro.aplicacao.multa.dto.UpdateMultaStatusRequestDTO;
import br.com.cesar.gestaoCondominial.financeiro.aplicacao.multa.usecase.ContestarMultaUseCase;
import br.com.cesar.gestaoCondominial.financeiro.aplicacao.multa.usecase.CreateMultaManualUseCase;
import br.com.cesar.gestaoCondominial.financeiro.aplicacao.multa.usecase.ListMultasByUnidadeUseCase;
import br.com.cesar.gestaoCondominial.financeiro.aplicacao.multa.usecase.RegistrarPagamentoMultaUseCase;
import br.com.cesar.gestaoCondominial.financeiro.aplicacao.multa.usecase.UpdateMultaStatusUseCase;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/multas")
public class MultaController {

    private final CreateMultaManualUseCase createMultaManualUseCase;
    private final RegistrarPagamentoMultaUseCase registrarPagamentoMultaUseCase;
    private final UpdateMultaStatusUseCase updateMultaStatusUseCase;
    private final ContestarMultaUseCase contestarMultaUseCase;
    private final ListMultasByUnidadeUseCase listMultasByUnidadeUseCase;

    public MultaController(CreateMultaManualUseCase createMultaManualUseCase, RegistrarPagamentoMultaUseCase registrarPagamentoMultaUseCase, UpdateMultaStatusUseCase updateMultaStatusUseCase, ContestarMultaUseCase contestarMultaUseCase, ListMultasByUnidadeUseCase listMultasByUnidadeUseCase) {
        this.createMultaManualUseCase = createMultaManualUseCase;
        this.registrarPagamentoMultaUseCase = registrarPagamentoMultaUseCase;
        this.updateMultaStatusUseCase = updateMultaStatusUseCase;
        this.contestarMultaUseCase = contestarMultaUseCase;
        this.listMultasByUnidadeUseCase = listMultasByUnidadeUseCase;
    }

    @PostMapping
        public ResponseEntity<MultaResponseDTO> criar(@Valid @RequestBody CreateMultaRequestDTO request) {
        MultaResponseDTO response = createMultaManualUseCase.execute(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/status")
        public ResponseEntity<MultaResponseDTO> atualizarStatus(@PathVariable Long id,@Valid @RequestBody UpdateMultaStatusRequestDTO request) {
        return ResponseEntity.ok(updateMultaStatusUseCase.execute(id, request));
    }

    @PostMapping("/{id}/pagamento")
        public ResponseEntity<MultaResponseDTO> registrarPagamento(@PathVariable Long id,@Valid @RequestBody RegistrarPagamentoRequestDTO request) {
        return ResponseEntity.ok(registrarPagamentoMultaUseCase.execute(id, request));
    }

    @PostMapping("/{id}/contestar")
    public ResponseEntity<MultaResponseDTO> contestar(@PathVariable Long id,@Valid @RequestBody ContestarMultaRequestDTO request) {
        return ResponseEntity.ok(contestarMultaUseCase.execute(id, request));
    }

    @GetMapping("/unidade/{unidadeId}")
    public ResponseEntity<List<MultaResponseDTO>> listarPorUnidade(@PathVariable Long unidadeId) {
        return ResponseEntity.ok(listMultasByUnidadeUseCase.execute(unidadeId));
    }
}
