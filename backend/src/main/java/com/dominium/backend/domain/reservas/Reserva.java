package com.dominium.backend.domain.reservas;

import com.dominium.backend.domain.areacomum.AreaComum;
import com.dominium.backend.domain.unidade.Unidade;
import com.dominium.backend.domain.usuario.Usuario;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")

public class Reserva {

    private Long id;
    private AreaComum areaid;
    private Unidade unidadeid;
    private Usuario usuarioid;
    private LocalDate dataReserva;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private StatusReserva status;

    public void cancelar(){
        this.status = StatusReserva.CANCELADA;
    }
}