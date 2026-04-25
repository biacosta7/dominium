package com.dominium.backend.domain.assembleia;

import com.dominium.backend.domain.shared.exceptions.DomainException;
import lombok.Builder;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Builder
public class Pauta {
    private String id;
    private String titulo;
    private String descricao;
    private Integer votosSim;
    private Integer votosNao;
    private Integer abstencoes;
    @Builder.Default
    private Set<String> unidadesVotantes = new HashSet<>();

    public static Pauta nova(String titulo, String descricao) {
        return Pauta.builder()
                .id(UUID.randomUUID().toString())
                .titulo(titulo)
                .descricao(descricao)
                .votosSim(0)
                .votosNao(0)
                .abstencoes(0)
                .build();
    }

    public void adicionarVoto(String unidadeId, TipoVoto tipo) {
        if (unidadesVotantes.contains(unidadeId)) {
            throw new DomainException("Esta unidade já votou nesta pauta.");
        }

        unidadesVotantes.add(unidadeId);

        if (tipo == TipoVoto.SIM) this.votosSim++;
        else if (tipo == TipoVoto.NAO) this.votosNao++;
        else this.abstencoes++;
    }
}