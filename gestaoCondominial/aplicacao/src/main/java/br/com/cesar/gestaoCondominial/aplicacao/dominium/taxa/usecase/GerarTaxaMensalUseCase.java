package br.com.cesar.gestaoCondominial.aplicacao.dominium.taxa.usecase;

import br.com.cesar.gestaoCondominial.aplicacao.dominium.taxa.dto.TaxaRequestDTO;
import br.com.cesar.gestaoCondominial.aplicacao.dominium.taxa.dto.TaxaResponseDTO;
import br.com.cesar.gestaoCondominial.dominio.dominium.taxa.TaxaCondominial;
import br.com.cesar.gestaoCondominial.dominio.dominium.taxa.repository.TaxaCondominialRepository;
import br.com.cesar.gestaoCondominial.dominio.dominium.unidade.UnidadeId;
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