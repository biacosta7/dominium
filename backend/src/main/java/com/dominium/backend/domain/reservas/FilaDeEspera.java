package com.dominium.backend.domain.reservas;

import com.dominium.backend.domain.areacomum.AreaComumId;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class FilaDeEspera {
    private String id;
    private AreaComumId areaComumId;
    private Long usuarioId;
    private LocalDateTime dataDesejada;
    private LocalDateTime dataCadastro;
    private StatusFila status;

    public enum StatusFila {
        AGUARDANDO, PROMOVIDO, CANCELADO
    }

    public static FilaDeEspera criar(AreaComumId areaId, Long usuarioId, LocalDateTime dataDesejada) {
        FilaDeEspera fila = new FilaDeEspera();
        fila.id = UUID.randomUUID().toString();
        fila.areaComumId = areaId;
        fila.usuarioId = usuarioId;
        fila.dataDesejada = dataDesejada;
        fila.dataCadastro = LocalDateTime.now();
        fila.status = StatusFila.AGUARDANDO;
        return fila;
    }
}