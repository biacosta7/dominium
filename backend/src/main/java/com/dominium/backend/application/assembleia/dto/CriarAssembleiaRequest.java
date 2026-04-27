package com.dominium.backend.application.assembleia.dto;

import java.time.LocalDateTime;
import java.util.List;

public record CriarAssembleiaRequest(
        String titulo,
        LocalDateTime dataHora,
        String local,
        List<String> pauta
) {}
