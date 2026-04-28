package br.com.cesar.gestaoCondominial.financeiro.aplicacao.financeiro.usecase;

import java.math.BigDecimal;
import org.springframework.stereotype.Service;

import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.Orcamento;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.repository.OrcamentoRepository;
import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.ResourceNotFoundException;

@Service
public class ConsultarSaldoUseCase {

    private final OrcamentoRepository orcamentoRepository;

    public ConsultarSaldoUseCase(OrcamentoRepository orcamentoRepository) {
        this.orcamentoRepository = orcamentoRepository;
    }

    public BigDecimal execute(Integer ano) {
        Orcamento orcamento = orcamentoRepository.findByAno(ano)
                .orElseThrow(() -> new ResourceNotFoundException("Orçamento não encontrado para o ano " + ano));
        
        return orcamento.getSaldoDisponivel();
    }
}
