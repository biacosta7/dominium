package br.com.cesar.gestaoCondominial.financeiro.aplicacao.taxa.usecase;

import br.com.cesar.gestaoCondominial.financeiro.aplicacao.taxa.dto.TaxaResponseDTO;
import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.ResourceNotFoundException;
import br.com.cesar.gestaoCondominial.financeiro.dominio.taxa.TaxaCondominial;
import br.com.cesar.gestaoCondominial.financeiro.dominio.taxa.TaxaId;
import br.com.cesar.gestaoCondominial.financeiro.dominio.taxa.repository.TaxaCondominialRepository;
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