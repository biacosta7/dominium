package br.com.cesar.gestaoCondominial.financeiro.aplicacao.taxa.usecase;

import br.com.cesar.gestaoCondominial.financeiro.aplicacao.taxa.dto.TaxaRequestDTO;
import br.com.cesar.gestaoCondominial.financeiro.aplicacao.taxa.dto.TaxaResponseDTO;
import br.com.cesar.gestaoCondominial.financeiro.dominio.taxa.TaxaCondominial;
import br.com.cesar.gestaoCondominial.financeiro.dominio.taxa.repository.TaxaCondominialRepository;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.UnidadeId;
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