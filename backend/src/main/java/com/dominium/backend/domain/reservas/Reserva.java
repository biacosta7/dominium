package com.dominium.backend.domain.reservas;

import com.dominium.backend.domain.areacomum.AreaComumId;
import com.dominium.backend.domain.unidade.UnidadeId;
import com.dominium.backend.domain.usuario.UsuarioId;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")

public class Reserva {

    private ReservaId id;
    private AreaComumId areaComumId;
    private UnidadeId unidadeId;
    private UsuarioId usuarioId;

    private LocalDate dataReserva;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private StatusReserva status;
    private LocalDateTime dataExpiraConfirmacao;

    // Factory method — único jeito de criar uma reserva nova
    public static Reserva criar(
            ReservaId id,
            AreaComumId areaComumId,
            UnidadeId unidadeId,
            UsuarioId usuarioId,
            LocalDate data,
            LocalTime inicio,
            LocalTime fim
    ) {
        Reserva r = new Reserva();
        r.id = id;
        r.areaComumId = areaComumId;
        r.unidadeId = unidadeId;
        r.usuarioId = usuarioId;
        r.dataReserva = data;
        r.horaInicio = inicio;
        r.horaFim = fim;
        r.status = StatusReserva.PENDENTE; // nunca fica null
        return r;
    }

    public static Reserva reconstituir(
            ReservaId id,
            AreaComumId areaComumId,
            UnidadeId unidadeId,
            UsuarioId usuarioId,
            LocalDate data,
            LocalTime inicio,
            LocalTime fim,
            StatusReserva status
    ) {
        Reserva r = new Reserva();
        r.id = id;
        r.areaComumId = areaComumId;
        r.unidadeId = unidadeId;
        r.usuarioId = usuarioId;
        r.dataReserva = data;
        r.horaInicio = inicio;
        r.horaFim = fim;
        r.status = status; // preserva o status real
        return r;
    }

    public static Reserva promoverDeFila(
            ReservaId id,
            AreaComumId areaComumId,
            UnidadeId unidadeId,
            UsuarioId usuarioId,
            LocalDate data,
            LocalTime inicio,
            LocalTime fim
    ) {
        Reserva r = new Reserva();
        r.id = id;
        r.areaComumId = areaComumId;
        r.unidadeId = unidadeId;
        r.usuarioId = usuarioId;
        r.dataReserva = data;
        r.horaInicio = inicio;
        r.horaFim = fim;
        r.status = StatusReserva.AGUARDANDO_CONFIRMACAO;
        r.dataExpiraConfirmacao = LocalDateTime.now().plusHours(24); // Prazo de 24h
        return r;
    }

    public void cancelar(){
        this.status = StatusReserva.CANCELADA;
    }

    public void ativar() {
        this.status = StatusReserva.ATIVA;
        this.dataExpiraConfirmacao = null;
    }

    public void confirmar() {
        if (this.status != StatusReserva.AGUARDANDO_CONFIRMACAO) {
            throw new IllegalStateException("Reserva não está aguardando confirmação");
        }
        if (LocalDateTime.now().isAfter(dataExpiraConfirmacao)) {
            this.status = StatusReserva.CANCELADA;
            throw new IllegalStateException("Prazo de confirmação expirado");
        }
        this.status = StatusReserva.ATIVA;
        this.dataExpiraConfirmacao = null;
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