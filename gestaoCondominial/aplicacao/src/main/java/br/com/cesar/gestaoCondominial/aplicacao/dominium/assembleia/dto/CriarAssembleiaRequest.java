package br.com.cesar.gestaoCondominial.aplicacao.dominium.assembleia.dto;

import java.time.LocalDateTime;
import java.util.List;

public record CriarAssembleiaRequest(
        String titulo,
        LocalDateTime dataHora,
        String local,
        List<String> pauta
) {}
