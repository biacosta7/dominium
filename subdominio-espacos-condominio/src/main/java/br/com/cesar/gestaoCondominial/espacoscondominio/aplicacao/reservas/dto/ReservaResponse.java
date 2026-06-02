package br.com.cesar.gestaoCondominial.espacoscondominio.aplicacao.reservas.dto;

import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.Reserva;
import java.time.LocalDate;
import java.time.LocalTime;

public record ReservaResponse(
        String id,
        Long areaComumId,
        String nomeArea,
        LocalDate data,
        LocalTime horaInicio,
        LocalTime horaFim,
        String status
) {
    public static ReservaResponse from(Reserva reserva) {
        String nome = "Área Comum " + (reserva.getAreaComumId() != null ? reserva.getAreaComumId().getValor() : "");
        if (reserva.getAreaComumId() != null) {
            long valor = reserva.getAreaComumId().getValor();
            if (valor == 1) nome = "Churrasqueira 1";
            else if (valor == 2) nome = "Churrasqueira 2";
            else if (valor == 3) nome = "Salão de Festas";
            else if (valor == 4) nome = "Piscina (Espaço Gourmet)";
        }

        return new ReservaResponse(
                reserva.getId() != null ? reserva.getId().getValor() : null,
                reserva.getAreaComumId() != null ? reserva.getAreaComumId().getValor() : null,
                nome,
                reserva.getDataReserva(),
                reserva.getHoraInicio(),
                reserva.getHoraFim(),
                reserva.getStatus() != null ? reserva.getStatus().name() : null
        );
    }
}