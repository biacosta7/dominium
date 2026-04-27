package br.com.cesar.gestaoCondominial.aplicacao.dominium.taxa.usecase;

import br.com.cesar.gestaoCondominial.aplicacao.dominium.taxa.dto.TaxaResponseDTO;
import br.com.cesar.gestaoCondominial.dominio.dominium.taxa.TaxaCondominial;
import br.com.cesar.gestaoCondominial.dominio.dominium.taxa.repository.TaxaCondominialRepository;
import br.com.cesar.gestaoCondominial.dominio.dominium.unidade.UnidadeId;
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