package br.com.cesar.gestaoCondominial.aplicacao.dominium.assembleia.dto;

import br.com.cesar.gestaoCondominial.dominio.dominium.assembleia.Assembleia;

import java.time.LocalDateTime;
import java.util.List;

public record AssembleiaResponse(
        Long id,
        String titulo,
        LocalDateTime dataHora,
        String local,
        List<String> pauta,
        String status,
        Long sindicoId,
        LocalDateTime dataCriacao
) {
    public static AssembleiaResponse from(Assembleia assembleia) {
        return new AssembleiaResponse(
                assembleia.getId().getValor(),
                assembleia.getTitulo(),
                assembleia.getDataHora(),
                assembleia.getLocal(),
                assembleia.getPauta(),
                assembleia.getStatus().name(),
                assembleia.getSindicoId(),
                assembleia.getDataCriacao()
        );
    }
}
