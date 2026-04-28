package br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Orcamento {
    private Long id;
    private Integer ano;
    private BigDecimal valorTotal;
    private BigDecimal valorGasto;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Orcamento() {
        this.valorGasto = BigDecimal.ZERO;
    }

    public Orcamento(Integer ano, BigDecimal valorTotal) {
        this.ano = ano;
        this.valorTotal = valorTotal;
        this.valorGasto = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public BigDecimal getValorGasto() {
        return valorGasto;
    }

    public void setValorGasto(BigDecimal valorGasto) {
        this.valorGasto = valorGasto;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public BigDecimal getSaldoDisponivel() {
        return valorTotal.subtract(valorGasto);
    }

    public void adicionarDespesa(BigDecimal valor) {
        this.valorGasto = this.valorGasto.add(valor);
    }
}
