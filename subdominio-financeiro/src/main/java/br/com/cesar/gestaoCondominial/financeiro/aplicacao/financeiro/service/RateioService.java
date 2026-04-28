package br.com.cesar.gestaoCondominial.financeiro.aplicacao.financeiro.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.Despesa;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.Unidade;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.repository.UnidadeRepository;

@Service
public class RateioService {

    private final UnidadeRepository unidadeRepository;

    public RateioService(UnidadeRepository unidadeRepository) {
        this.unidadeRepository = unidadeRepository;
    }

    public void realizarRateio(Despesa despesa) {
        List<Unidade> unidades = unidadeRepository.findAll();
        if (unidades.isEmpty()) return;

        BigDecimal valorPorUnidade = despesa.getValor().divide(BigDecimal.valueOf(unidades.size()), 2, RoundingMode.HALF_UP);

        for (Unidade unidade : unidades) {
            BigDecimal novoSaldo = unidade.getSaldoDevedor().add(valorPorUnidade);
            unidade.setSaldoDevedor(novoSaldo);
            unidadeRepository.save(unidade);
        }
    }
}
