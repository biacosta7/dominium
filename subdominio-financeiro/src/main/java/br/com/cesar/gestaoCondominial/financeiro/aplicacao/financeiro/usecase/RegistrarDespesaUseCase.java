package br.com.cesar.gestaoCondominial.financeiro.aplicacao.financeiro.usecase;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.Despesa;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.Orcamento;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.TipoDespesa;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.CategoriaDespesa;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.StatusDespesa;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.repository.DespesaRepository;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.repository.OrcamentoRepository;
import br.com.cesar.gestaoCondominial.financeiro.aplicacao.financeiro.service.RateioService;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.strategy.PoliticaFinanceiraStrategy;
import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.DomainException;
import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.ResourceNotFoundException;

@Service
public class RegistrarDespesaUseCase {

    private final DespesaRepository despesaRepository;
    private final OrcamentoRepository orcamentoRepository;
    private final RateioService rateioService;
    private final PoliticaFinanceiraStrategy politicaFinanceira;

    public RegistrarDespesaUseCase(DespesaRepository despesaRepository,
            OrcamentoRepository orcamentoRepository,
            RateioService rateioService,
            PoliticaFinanceiraStrategy politicaFinanceira) {
        this.despesaRepository = despesaRepository;
        this.orcamentoRepository = orcamentoRepository;
        this.rateioService = rateioService;
        this.politicaFinanceira = politicaFinanceira;
    }

    @Transactional
    public Despesa execute(String descricao, BigDecimal valor, LocalDate data, CategoriaDespesa categoria,
            TipoDespesa tipo) {
        int ano = data.getYear();
        Orcamento orcamento = orcamentoRepository.findByAno(ano)
                .orElseThrow(() -> new ResourceNotFoundException("Orçamento não encontrado para o ano " + ano));

        if (orcamento.getSaldoDisponivel().compareTo(valor) < 0) {
            throw new DomainException("Saldo insuficiente no orçamento para esta despesa. Saldo disponível: "
                    + orcamento.getSaldoDisponivel());
        }

        Despesa despesa = new Despesa(descricao, valor, data, categoria, tipo, StatusDespesa.PENDENTE,
                orcamento.getId());

        StatusDespesa statusInicial = politicaFinanceira.determinarStatusInicial(despesa, orcamento);
        despesa.setStatus(statusInicial);

        if (despesa.getStatus() == StatusDespesa.APROVADA) {
            orcamento.adicionarDespesa(valor);
            orcamentoRepository.save(orcamento);

            if (tipo == TipoDespesa.EXTRAORDINARIA) {
                rateioService.realizarRateio(despesa);
            }
        }

        return despesaRepository.save(despesa);
    }
}
