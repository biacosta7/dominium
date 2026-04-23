package com.dominium.backend.domain.reservas;

import com.dominium.backend.domain.areacomum.AreaComumId;
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

    private ReservaId id;
    private AreaComumId areaComumId;
    private Unidade unidadeId;
    private Usuario usuarioId;

    private LocalDate dataReserva;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private StatusReserva status;

    public void cancelar(){
        this.status = StatusReserva.CANCELADA;
    }

    public void ativar() {
        this.status = StatusReserva.ATIVA;
    }

    public void atualizarDados(
            LocalDate data,
            LocalTime inicio,
            LocalTime fim
    ) {
        this.dataReserva = data;
        this.horaInicio = inicio;
        this.horaFim = fim;
    }

    public boolean conflitoCom(Reserva outra){
        if(!this.dataReserva.equals(outra.dataReserva)){
            return false;
        }
        return this.horaInicio.isBefore(outra.getHoraFim()) &&
                outra.getHoraInicio().isBefore(this.horaFim);
    }

}