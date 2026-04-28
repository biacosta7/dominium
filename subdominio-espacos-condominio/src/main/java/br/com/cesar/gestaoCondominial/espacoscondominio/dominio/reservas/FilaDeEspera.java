package br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas;

import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.areacomum.AreaComumId;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.UsuarioId;
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
    private FilaDeEsperaId id;
    private AreaComumId areaComumId;
    private UsuarioId usuarioId;
    private LocalDate dataDesejada;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private LocalDateTime dataCadastro;
    private StatusFila status;

    public enum StatusFila {
        AGUARDANDO, PROMOVIDO, CANCELADO
    }

    public static FilaDeEspera criar(
            FilaDeEsperaId id,
            AreaComumId areaId,
            UsuarioId usuarioId,
            LocalDate data,
            LocalTime inicio,
            LocalTime fim
    ) {
        FilaDeEspera fila = new FilaDeEspera();
        fila.id = id;
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