package br.com.cesar.gestaoCondominial.governanca.aplicacao.assembleia.dto;

import java.time.LocalDateTime;
import java.util.List;

public record CriarAssembleiaRequest(
        String titulo,
        LocalDateTime dataHora,
        String local,
        List<String> pauta
) {}
