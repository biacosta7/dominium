package com.dominium.backend.domain.ocorrencia;

import com.dominium.backend.domain.unidade.UnidadeId;
import com.dominium.backend.domain.usuario.Usuario;
import java.time.LocalDateTime;

public class Ocorrencia {
    private Long id;
    private String descricao;
    private UnidadeId unidadeId;
    private Usuario relator;
    private LocalDateTime dataRegistro;
    private StatusOcorrencia status;
    private boolean geraMulta;
    private String observacaoSindico;

    public Ocorrencia() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public UnidadeId getUnidadeId() { return unidadeId; }
    public void setUnidadeId(UnidadeId unidadeId) { this.unidadeId = unidadeId; }

    public Usuario getRelator() { return relator; }
    public void setRelator(Usuario relator) { this.relator = relator; }

    public LocalDateTime getDataRegistro() { return dataRegistro; }
    public void setDataRegistro(LocalDateTime dataRegistro) { this.dataRegistro = dataRegistro; }

    public StatusOcorrencia getStatus() { return status; }
    public void setStatus(StatusOcorrencia status) { this.status = status; }

    public boolean isGeraMulta() { return geraMulta; }
    public void setGeraMulta(boolean geraMulta) { this.geraMulta = geraMulta; }

    public String getObservacaoSindico() { return observacaoSindico; }
    public void setObservacaoSindico(String observacaoSindico) { this.observacaoSindico = observacaoSindico; }

    public enum StatusOcorrencia {
        ABERTA, EM_ANALISE, ENCERRADA
    }
}