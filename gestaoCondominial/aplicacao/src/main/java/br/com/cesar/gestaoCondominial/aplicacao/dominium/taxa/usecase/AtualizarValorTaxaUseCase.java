package br.com.cesar.gestaoCondominial.aplicacao.dominium.taxa.usecase;

import br.com.cesar.gestaoCondominial.aplicacao.dominium.taxa.dto.AtualizarTaxaRequestDTO;
import br.com.cesar.gestaoCondominial.aplicacao.dominium.taxa.dto.TaxaResponseDTO;
import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.ResourceNotFoundException;
import br.com.cesar.gestaoCondominial.dominio.dominium.taxa.TaxaCondominial;
import br.com.cesar.gestaoCondominial.dominio.dominium.taxa.TaxaId;
import br.com.cesar.gestaoCondominial.dominio.dominium.taxa.repository.TaxaCondominialRepository;
import org.springframework.stereotype.Service;

@Service
public class AtualizarValorTaxaUseCase {

    private final TaxaCondominialRepository repository;

    public AtualizarValorTaxaUseCase(TaxaCondominialRepository repository) {
        this.repository = repository;
    }

    public TaxaResponseDTO executar(Long taxaIdValor, AtualizarTaxaRequestDTO request) {
        TaxaId taxaId = new TaxaId(taxaIdValor);
        TaxaCondominial taxa = repository.buscarPorId(taxaId)
                .orElseThrow(() -> new ResourceNotFoundException("Taxa Condominial não encontrada."));

        taxa.atualizarValor(request.getNovoValorBase(), request.getNovasMultas());
        repository.atualizar(taxa);

        return TaxaResponseDTO.fromDomain(taxa);
    }
}