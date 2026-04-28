package br.com.cesar.gestaoCondominial.financeiro.aplicacao.financeiro.usecase;

import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.Orcamento;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.repository.OrcamentoRepository;
import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.DomainException;

@Service
public class CadastrarOrcamentoUseCase {

    private final OrcamentoRepository orcamentoRepository;

    public CadastrarOrcamentoUseCase(OrcamentoRepository orcamentoRepository) {
        this.orcamentoRepository = orcamentoRepository;
    }

    @Transactional
    public Orcamento execute(Integer ano, BigDecimal valorTotal) {
        if (orcamentoRepository.findByAno(ano).isPresent()) {
            throw new DomainException("Já existe um orçamento cadastrado para o ano " + ano);
        }

        Orcamento orcamento = new Orcamento(ano, valorTotal);
        return orcamentoRepository.save(orcamento);
    }
}
