package br.com.cesar.gestaoCondominial.financeiro.aplicacao.financeiro.usecase;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.Orcamento;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.repository.OrcamentoRepository;

@Service
public class ListOrcamentosUseCase {

    private final OrcamentoRepository orcamentoRepository;

    public ListOrcamentosUseCase(OrcamentoRepository orcamentoRepository) {
        this.orcamentoRepository = orcamentoRepository;
    }

    public List<Orcamento> execute() {
        return orcamentoRepository.findAll();
    }
}
