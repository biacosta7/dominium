package br.com.cesar.gestaoCondominial.aplicacao.dominium.assembleia.dto;

import java.time.LocalDateTime;
import java.util.List;

public record EditarAssembleiaRequest(
        String titulo,
        LocalDateTime dataHora,
        String local,
        List<String> pauta
) {}
