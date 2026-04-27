package com.dominium.backend.application.taxa.usecase;

import com.dominium.backend.application.taxa.dto.TaxaResponseDTO;
import com.dominium.backend.domain.shared.exceptions.ResourceNotFoundException;
import com.dominium.backend.domain.taxa.TaxaCondominial;
import com.dominium.backend.domain.taxa.TaxaId;
import com.dominium.backend.domain.taxa.repository.TaxaCondominialRepository;
import org.springframework.stereotype.Service;

@Service
public class RegistrarPagamentoTaxaUseCase {

    private final TaxaCondominialRepository repository;

    public RegistrarPagamentoTaxaUseCase(TaxaCondominialRepository repository) {
        this.repository = repository;
    }

    public TaxaResponseDTO executar(Long taxaIdValor) {
        TaxaId taxaId = new TaxaId(taxaIdValor);
        TaxaCondominial taxa = repository.buscarPorId(taxaId)
                .orElseThrow(() -> new ResourceNotFoundException("Taxa Condominial não encontrada."));

        taxa.registrarPagamento();
        repository.atualizar(taxa);

        return TaxaResponseDTO.fromDomain(taxa);
    }
}