package com.dominium.backend.application.taxa.usecase;

import com.dominium.backend.application.taxa.dto.TaxaResponseDTO;
import com.dominium.backend.domain.taxa.TaxaCondominial;
import com.dominium.backend.domain.taxa.repository.TaxaCondominialRepository;
import com.dominium.backend.domain.unidade.UnidadeId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConsultarHistoricoTaxasUseCase {

    private final TaxaCondominialRepository repository;

    public ConsultarHistoricoTaxasUseCase(TaxaCondominialRepository repository) {
        this.repository = repository;
    }

    public List<TaxaResponseDTO> executar(Long unidadeIdValor) {
        UnidadeId unidadeId = new UnidadeId(unidadeIdValor);
        List<TaxaCondominial> taxas = repository.listarPorUnidade(unidadeId);

        return taxas.stream()
                .map(TaxaResponseDTO::fromDomain)
                .collect(Collectors.toList());
    }
}