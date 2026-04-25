package com.dominium.backend.application.assembleia.dto;

import com.dominium.backend.domain.assembleia.Assembleia;

import java.time.LocalDateTime;
import java.util.List;

public record AssembleiaResponse(
        String id,
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
