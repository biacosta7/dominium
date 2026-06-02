package br.com.cesar.gestaoCondominial.espacoscondominio.aplicacao.reservas.dto;

public record CriarReservaRequest(
        Long areaComumId,
        Long unidadeId,
        Long usuarioId,
        String data,
        String horaInicio,
        String horaFim
) {}