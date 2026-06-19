package br.com.cesar.gestaoCondominial.espacoscondominio.aplicacao.reservas.dto;

import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.FilaDeEspera;

import java.time.LocalDate;
import java.time.LocalTime;

public record FilaResponseDTO(
        String id,
        Long areaComumId,
        String nomeArea,
        LocalDate dataDesejada,
        LocalTime horaInicio,
        LocalTime horaFim,
        String status,
        int posicao
) {
    public static FilaResponseDTO from(FilaDeEspera fila, int posicao) {
        return new FilaResponseDTO(
                fila.getId().getValor(),
                fila.getAreaComumId().getValor(),
                nomeArea(fila.getAreaComumId().getValor()),
                fila.getDataDesejada(),
                fila.getHoraInicio(),
                fila.getHoraFim(),
                fila.getStatus().name(),
                posicao
        );
    }

    private static String nomeArea(long valor) {
        if (valor == 1) return "Churrasqueira 1";
        if (valor == 2) return "Churrasqueira 2";
        if (valor == 3) return "Salão de Festas";
        if (valor == 4) return "Piscina (Espaço Gourmet)";
        return "Área Comum " + valor;
    }
}
