package br.com.cesar.gestaoCondominial.financeiro.aplicacao.taxa.usecase;

import br.com.cesar.gestaoCondominial.financeiro.aplicacao.taxa.dto.TaxaResponseDTO;
import br.com.cesar.gestaoCondominial.financeiro.dominio.taxa.TaxaCondominial;
import br.com.cesar.gestaoCondominial.financeiro.dominio.taxa.repository.TaxaCondominialRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ListarTodasTaxasUseCase {

    private final TaxaCondominialRepository repository;

    public ListarTodasTaxasUseCase(TaxaCondominialRepository repository) {
        this.repository = repository;
    }

    public List<TaxaResponseDTO> executar() {
        List<TaxaCondominial> taxas = repository.listarTodas();
        return taxas.stream()
                .map(TaxaResponseDTO::fromDomain)
                .collect(Collectors.toList());
    }
}
