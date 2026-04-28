package br.com.cesar.gestaoCondominial.aplicacao.dominium.reservas.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class FilaRequestDTO {
    private Long areaComumId;
    private Long usuarioId;
    private LocalDate data;
    private LocalTime horaInicio;
    private LocalTime horaFim;
}