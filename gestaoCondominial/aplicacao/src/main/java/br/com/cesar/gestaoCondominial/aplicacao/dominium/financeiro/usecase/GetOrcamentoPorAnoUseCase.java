package br.com.cesar.gestaoCondominial.aplicacao.dominium.financeiro.usecase;

import org.springframework.stereotype.Service;

import br.com.cesar.gestaoCondominial.dominio.dominium.financeiro.Orcamento;
import br.com.cesar.gestaoCondominial.dominio.dominium.financeiro.repository.OrcamentoRepository;
import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.ResourceNotFoundException;

@Service
public class GetOrcamentoPorAnoUseCase {

    private final OrcamentoRepository orcamentoRepository;

    public GetOrcamentoPorAnoUseCase(OrcamentoRepository orcamentoRepository) {
        this.orcamentoRepository = orcamentoRepository;
    }

    public Orcamento execute(Integer ano) {
        return orcamentoRepository.findByAno(ano)
                .orElseThrow(() -> new ResourceNotFoundException("Orçamento não encontrado para o ano: " + ano));
    }
}
