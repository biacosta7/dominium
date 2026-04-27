package com.dominium.backend.domain.documento;

import com.dominium.backend.domain.shared.exceptions.DomainException;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Documento {

    private DocumentoId id;
    private String nome;
    private CategoriaDocumento categoria;
    private StatusDocumento status;
    private LocalDate dataValidade;
    private Long sindicoId;
    private LocalDateTime dataCriacao;

    public static Documento criar(DocumentoId id, String nome, CategoriaDocumento categoria,
                                   LocalDate dataValidade, Long sindicoId) {
        Documento d = new Documento();
        d.id = id;
        d.nome = nome;
        d.categoria = categoria;
        d.status = StatusDocumento.ATIVO;
        d.dataValidade = dataValidade;
        d.sindicoId = sindicoId;
        d.dataCriacao = LocalDateTime.now();
        return d;
    }

    public static Documento reconstituir(DocumentoId id, String nome, CategoriaDocumento categoria,
                                          StatusDocumento status, LocalDate dataValidade,
                                          Long sindicoId, LocalDateTime dataCriacao) {
        Documento d = new Documento();
        d.id = id;
        d.nome = nome;
        d.categoria = categoria;
        d.status = status;
        d.dataValidade = dataValidade;
        d.sindicoId = sindicoId;
        d.dataCriacao = dataCriacao;
        return d;
    }

    public void inativar() {
        if (this.status == StatusDocumento.INATIVO) {
            throw new DomainException("Documento já está inativo");
        }
        this.status = StatusDocumento.INATIVO;
    }

    public boolean isAtivo() {
        return this.status == StatusDocumento.ATIVO;
    }

    public boolean venceEm(int dias) {
        if (dataValidade == null) return false;
        LocalDate limite = LocalDate.now().plusDays(dias);
        return !dataValidade.isAfter(limite) && !dataValidade.isBefore(LocalDate.now());
    }
}
