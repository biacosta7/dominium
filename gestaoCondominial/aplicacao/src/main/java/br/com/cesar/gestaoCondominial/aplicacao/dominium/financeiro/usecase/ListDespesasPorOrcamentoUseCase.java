package br.com.cesar.gestaoCondominial.aplicacao.dominium.financeiro.usecase;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import br.com.cesar.gestaoCondominial.dominio.dominium.financeiro.CategoriaDespesa;
import br.com.cesar.gestaoCondominial.dominio.dominium.financeiro.Despesa;
import br.com.cesar.gestaoCondominial.dominio.dominium.financeiro.Orcamento;
import br.com.cesar.gestaoCondominial.dominio.dominium.financeiro.repository.DespesaRepository;
import br.com.cesar.gestaoCondominial.dominio.dominium.financeiro.repository.OrcamentoRepository;
import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.ResourceNotFoundException;

@Service
public class ListDespesasPorOrcamentoUseCase {

    private final DespesaRepository despesaRepository;
    private final OrcamentoRepository orcamentoRepository;

    public ListDespesasPorOrcamentoUseCase(DespesaRepository despesaRepository, OrcamentoRepository orcamentoRepository) {
        this.despesaRepository = despesaRepository;
        this.orcamentoRepository = orcamentoRepository;
    }

    public List<Despesa> execute(Integer ano, CategoriaDespesa categoria) {
        Orcamento orcamento = orcamentoRepository.findByAno(ano)
                .orElseThrow(() -> new ResourceNotFoundException("Orçamento não encontrado para o ano: " + ano));

        List<Despesa> despesas = despesaRepository.findByOrcamentoId(orcamento.getId());

        if (categoria != null) {
            return despesas.stream()
                    .filter(d -> d.getCategoria() == categoria)
                    .collect(Collectors.toList());
        }

        return despesas;
    }
}
