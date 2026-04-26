package com.dominium.backend.domain.taxa;

import com.dominium.backend.domain.unidade.UnidadeId;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class TaxaCondominial {
    private TaxaId id;
    private UnidadeId unidadeId;
    private BigDecimal valorBase;
    private BigDecimal valorMultas;
    private BigDecimal valorTotal;
    private LocalDate dataVencimento;
    private LocalDateTime dataPagamento;
    private StatusTaxa status;

    public TaxaCondominial(UnidadeId unidadeId, BigDecimal valorBase, BigDecimal valorMultas, LocalDate dataVencimento) {
        this.unidadeId = unidadeId;
        this.valorBase = valorBase;
        this.valorMultas = valorMultas != null ? valorMultas : BigDecimal.ZERO;
        this.valorTotal = this.valorBase.add(this.valorMultas);
        this.dataVencimento = dataVencimento;
        this.status = StatusTaxa.PENDENTE;
    }

    public TaxaCondominial(TaxaId id, UnidadeId unidadeId, BigDecimal valorBase, BigDecimal valorMultas,
                           BigDecimal valorTotal, LocalDate dataVencimento, LocalDateTime dataPagamento, StatusTaxa status) {
        this.id = id;
        this.unidadeId = unidadeId;
        this.valorBase = valorBase;
        this.valorMultas = valorMultas;
        this.valorTotal = valorTotal;
        this.dataVencimento = dataVencimento;
        this.dataPagamento = dataPagamento;
        this.status = status;
    }

    public void setId(TaxaId id) {
        this.id = id;
    }

    public void registrarPagamento() {
        if (this.status == StatusTaxa.PAGA) {
            throw new IllegalStateException("Esta taxa já consta como paga.");
        }
        this.status = StatusTaxa.PAGA;
        this.dataPagamento = LocalDateTime.now();
    }

    public void atualizarValor(BigDecimal novoValorBase, BigDecimal novasMultas) {
        if (this.status == StatusTaxa.PAGA) {
            throw new IllegalStateException("Não é possível alterar o valor de uma taxa que já foi paga.");
        }
        this.valorBase = novoValorBase;
        this.valorMultas = novasMultas != null ? novasMultas : BigDecimal.ZERO;
        this.valorTotal = this.valorBase.add(this.valorMultas);
    }

    public void verificarAtraso() {
        if (this.status == StatusTaxa.PENDENTE && LocalDate.now().isAfter(this.dataVencimento)) {
            this.status = StatusTaxa.ATRASADA;
        }
    }
}