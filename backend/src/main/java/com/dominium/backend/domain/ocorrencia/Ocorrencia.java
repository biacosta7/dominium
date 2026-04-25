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
    private TipoPenalidade penalidade;
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

    public TipoPenalidade getPenalidade() { return penalidade; }
    public void setPenalidade(TipoPenalidade penalidade) { this.penalidade = penalidade; }

    public String getObservacaoSindico() { return observacaoSindico; }
    public void setObservacaoSindico(String observacaoSindico) { this.observacaoSindico = observacaoSindico; }

    public enum StatusOcorrencia {
        ABERTA, EM_ANALISE, ENCERRADA
    }

    public void atualizarStatus(StatusOcorrencia novoStatus) {
        if (this.status == StatusOcorrencia.ENCERRADA) {
            throw new IllegalStateException("Não é possível alterar o status de uma ocorrência encerrada.");
        }
        this.status = novoStatus;
    }

    public void encerrar(TipoPenalidade penalidadeAplicada, String observacao) {
        if (this.status == StatusOcorrencia.ENCERRADA) {
            throw new IllegalStateException("A ocorrência já está encerrada.");
        }
        this.status = StatusOcorrencia.ENCERRADA;
        this.penalidade = penalidadeAplicada != null ? penalidadeAplicada : TipoPenalidade.NENHUMA;
        if (observacao != null && !observacao.trim().isEmpty()) {
            this.observacaoSindico = observacao;
        }
    }
}