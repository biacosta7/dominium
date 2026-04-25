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

    // Factory method — único jeito de criar uma reserva nova
    public static Reserva criar(
            ReservaId id,
            AreaComumId areaComumId,
            Unidade unidade,
            Usuario usuario,
            LocalDate data,
            LocalTime inicio,
            LocalTime fim
    ) {
        Reserva r = new Reserva();
        r.id = id;
        r.areaComumId = areaComumId;
        r.unidadeId = unidade;
        r.usuarioId = usuario;
        r.dataReserva = data;
        r.horaInicio = inicio;
        r.horaFim = fim;
        r.status = StatusReserva.PENDENTE; // nunca fica null
        return r;
    }

    public static Reserva reconstituir(
            ReservaId id,
            AreaComumId areaComumId,
            Unidade unidade,
            Usuario usuario,
            LocalDate data,
            LocalTime inicio,
            LocalTime fim,
            StatusReserva status
    ) {
        Reserva r = new Reserva();
        r.id = id;
        r.areaComumId = areaComumId;
        r.unidadeId = unidade;
        r.usuarioId = usuario;
        r.dataReserva = data;
        r.horaInicio = inicio;
        r.horaFim = fim;
        r.status = status; // preserva o status real
        return r;
    }

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
        if (outra.status == StatusReserva.CANCELADA ||
                outra.status == StatusReserva.CONCLUIDA) {
            return false;
        }

        if (!this.dataReserva.equals(outra.dataReserva)) {
            return false;
        }

        return this.horaInicio.isBefore(outra.horaFim) &&
                outra.horaInicio.isBefore(this.horaFim);
    }

}