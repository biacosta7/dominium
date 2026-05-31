package br.com.cesar.gestaoCondominial.financeiro.aplicacao.multa.observer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import br.com.cesar.gestaoCondominial.financeiro.dominio.multa.Multa;
import br.com.cesar.gestaoCondominial.financeiro.dominio.multa.MultaEventListener;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.StatusAdimplencia;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.Unidade;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.repository.UnidadeRepository;

@Component
public class UnidadeAdimplenciaListener implements MultaEventListener {

    private final UnidadeRepository unidadeRepository;

    public UnidadeAdimplenciaListener(UnidadeRepository unidadeRepository) {
        this.unidadeRepository = unidadeRepository;
    }

    @Override
    public void onMultaCriada(Multa multa) {
        Unidade unidade = multa.getUnidade();
        BigDecimal saldoAtual = unidade.getSaldoDevedor() != null ? unidade.getSaldoDevedor() : BigDecimal.ZERO;
        unidade.setSaldoDevedor(saldoAtual.add(multa.getValor()));
        unidade.setStatus(StatusAdimplencia.INADIMPLENTE);
        unidade.setUpdatedAt(LocalDateTime.now());
        unidadeRepository.save(unidade);
    }

    @Override
    public void onMultaPaga(Multa multa) {
        Unidade unidade = multa.getUnidade();
        BigDecimal saldoAtual = unidade.getSaldoDevedor() != null ? unidade.getSaldoDevedor() : BigDecimal.ZERO;
        BigDecimal novoSaldo = saldoAtual.subtract(multa.getValorPago());
        if (novoSaldo.compareTo(BigDecimal.ZERO) <= 0) {
            novoSaldo = BigDecimal.ZERO;
            unidade.setStatus(StatusAdimplencia.ADIMPLENTE);
        }
        unidade.setSaldoDevedor(novoSaldo);
        unidade.setUpdatedAt(LocalDateTime.now());
        unidadeRepository.save(unidade);
    }

    @Override
    public void onMultaCancelada(Multa multa) {
        Unidade unidade = multa.getUnidade();
        BigDecimal saldoAtual = unidade.getSaldoDevedor() != null ? unidade.getSaldoDevedor() : BigDecimal.ZERO;
        BigDecimal novoSaldo = saldoAtual.subtract(multa.getValor());
        if (novoSaldo.compareTo(BigDecimal.ZERO) <= 0) {
            novoSaldo = BigDecimal.ZERO;
            unidade.setStatus(StatusAdimplencia.ADIMPLENTE);
        }
        unidade.setSaldoDevedor(novoSaldo);
        unidade.setUpdatedAt(LocalDateTime.now());
        unidadeRepository.save(unidade);
    }
}
