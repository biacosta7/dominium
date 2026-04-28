package br.com.cesar.gestaoCondominial.financeiro.aplicacao.taxa.usecase;

import br.com.cesar.gestaoCondominial.financeiro.aplicacao.taxa.dto.TaxaResponseDTO;
import br.com.cesar.gestaoCondominial.financeiro.dominio.taxa.TaxaCondominial;
import br.com.cesar.gestaoCondominial.financeiro.dominio.taxa.repository.TaxaCondominialRepository;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.UnidadeId;
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