package com.dominium.backend.domain.reservas;

import com.dominium.backend.domain.areacomum.AreaComumId;
import lombok.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
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
    private LocalDate dataDesejada;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private LocalDateTime dataCadastro;
    private StatusFila status;

    public enum StatusFila {
        AGUARDANDO, PROMOVIDO, CANCELADO
    }

    public static FilaDeEspera criar(AreaComumId areaId, Long usuarioId, LocalDate data, LocalTime inicio, LocalTime fim) {
        FilaDeEspera fila = new FilaDeEspera();
        fila.id = UUID.randomUUID().toString();
        fila.areaComumId = areaId;
        fila.usuarioId = usuarioId;
        fila.dataDesejada = data;
        fila.horaInicio = inicio;
        fila.horaFim = fim;
        fila.dataCadastro = LocalDateTime.now();
        fila.status = StatusFila.AGUARDANDO;
        return fila;
    }
}