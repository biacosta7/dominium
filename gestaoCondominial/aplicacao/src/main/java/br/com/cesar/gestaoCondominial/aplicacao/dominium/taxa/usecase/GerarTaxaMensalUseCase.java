package com.dominium.backend.application.taxa.usecase;

import com.dominium.backend.application.taxa.dto.TaxaRequestDTO;
import com.dominium.backend.application.taxa.dto.TaxaResponseDTO;
import com.dominium.backend.domain.taxa.TaxaCondominial;
import com.dominium.backend.domain.taxa.repository.TaxaCondominialRepository;
import com.dominium.backend.domain.unidade.UnidadeId;
import org.springframework.stereotype.Service;

@Service
public class GerarTaxaMensalUseCase {

    private final TaxaCondominialRepository repository;

    public GerarTaxaMensalUseCase(TaxaCondominialRepository repository) {
        this.repository = repository;
    }

    public TaxaResponseDTO executar(TaxaRequestDTO request) {
        TaxaCondominial novaTaxa = new TaxaCondominial(
                new UnidadeId(request.getUnidadeId()),
                request.getValorBase(),
                request.getValorMultas(),
                request.getDataVencimento()
        );

        repository.salvar(novaTaxa);
        return TaxaResponseDTO.fromDomain(novaTaxa);
    }
}