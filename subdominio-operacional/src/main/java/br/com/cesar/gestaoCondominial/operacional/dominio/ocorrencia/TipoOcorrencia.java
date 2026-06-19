package br.com.cesar.gestaoCondominial.operacional.dominio.ocorrencia;

import java.math.BigDecimal;

public class TipoOcorrencia {
    private Long id;
    private String nome;
    private BigDecimal valorBaseMulta;

    public TipoOcorrencia() {}

    public TipoOcorrencia(Long id, String nome, BigDecimal valorBaseMulta) {
        this.id = id;
        this.nome = nome;
        this.valorBaseMulta = valorBaseMulta;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public BigDecimal getValorBaseMulta() { return valorBaseMulta; }
    public void setValorBaseMulta(BigDecimal valorBaseMulta) { this.valorBaseMulta = valorBaseMulta; }
}
