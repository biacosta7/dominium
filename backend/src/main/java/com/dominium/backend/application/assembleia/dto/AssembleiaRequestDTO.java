package com.dominium.backend.application.assembleia.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AssembleiaRequestDTO {
    private LocalDateTime dataHora;
    private String local;
    private List<PautaRequestDTO> pautas;
}