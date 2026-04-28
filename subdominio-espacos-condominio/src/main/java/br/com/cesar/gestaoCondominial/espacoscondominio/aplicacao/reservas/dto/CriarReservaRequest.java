package br.com.cesar.gestaoCondominial.espacoscondominio.aplicacao.reservas.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record CriarReservaRequest(
        Long areaComumId,
        Long unidadeId,
        Long usuarioId,
        LocalDate data,
        LocalTime horaInicio,
        LocalTime horaFim
) {}