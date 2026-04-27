package com.dominium.backend.presentation.taxa;

import com.dominium.backend.application.taxa.dto.AtualizarTaxaRequestDTO;
import com.dominium.backend.application.taxa.dto.TaxaRequestDTO;
import com.dominium.backend.application.taxa.dto.TaxaResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/taxas")
public class TaxaCondominialController {

    private final GerarTaxaMensalUseCase gerarTaxaMensalUseCase;
    private final AtualizarValorTaxaUseCase atualizarValorTaxaUseCase;
    private final RegistrarPagamentoTaxaUseCase registrarPagamentoTaxaUseCase;
    private final ConsultarHistoricoTaxasUseCase consultarHistoricoTaxasUseCase;

    public TaxaCondominialController(
            GerarTaxaMensalUseCase gerarTaxaMensalUseCase,
            AtualizarValorTaxaUseCase atualizarValorTaxaUseCase,
            RegistrarPagamentoTaxaUseCase registrarPagamentoTaxaUseCase,
            ConsultarHistoricoTaxasUseCase consultarHistoricoTaxasUseCase) {
        this.gerarTaxaMensalUseCase = gerarTaxaMensalUseCase;
        this.atualizarValorTaxaUseCase = atualizarValorTaxaUseCase;
        this.registrarPagamentoTaxaUseCase = registrarPagamentoTaxaUseCase;
        this.consultarHistoricoTaxasUseCase = consultarHistoricoTaxasUseCase;
    }

    @PostMapping
    public ResponseEntity<TaxaResponseDTO> gerarTaxa(@RequestBody TaxaRequestDTO request) {
        TaxaResponseDTO response = gerarTaxaMensalUseCase.executar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/valor")
    public ResponseEntity<TaxaResponseDTO> atualizarValor(@PathVariable Long id, @RequestBody AtualizarTaxaRequestDTO request) {
        TaxaResponseDTO response = atualizarValorTaxaUseCase.executar(id, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/pagamento")
    public ResponseEntity<TaxaResponseDTO> registrarPagamento(@PathVariable Long id) {
        TaxaResponseDTO response = registrarPagamentoTaxaUseCase.executar(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/unidade/{unidadeId}")
    public ResponseEntity<List<TaxaResponseDTO>> consultarHistorico(@PathVariable Long unidadeId) {
        List<TaxaResponseDTO> response = consultarHistoricoTaxasUseCase.executar(unidadeId);
        return ResponseEntity.ok(response);
    }
}