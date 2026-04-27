package com.dominium.backend.domain.assembleia;

import com.dominium.backend.domain.shared.exceptions.DomainException;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Assembleia {

    private static final int ANTECEDENCIA_MINIMA_DIAS = 5;

    private AssembleiaId id;
    private String titulo;
    private LocalDateTime dataHora;
    private String local;
    private List<String> pauta;
    private StatusAssembleia status;
    private Long sindicoId;
    private LocalDateTime dataCriacao;

    public static Assembleia criar(
            AssembleiaId id,
            String titulo,
            LocalDateTime dataHora,
            String local,
            List<String> pauta,
            Long sindicoId
    ) {
        validarAntecedencia(dataHora);
        Assembleia a = new Assembleia();
        a.id = id;
        a.titulo = titulo;
        a.dataHora = dataHora;
        a.local = local;
        a.pauta = pauta != null ? pauta : List.of();
        a.status = StatusAssembleia.AGENDADA;
        a.sindicoId = sindicoId;
        a.dataCriacao = LocalDateTime.now();
        return a;
    }

    public static Assembleia reconstituir(
            AssembleiaId id,
            String titulo,
            LocalDateTime dataHora,
            String local,
            List<String> pauta,
            StatusAssembleia status,
            Long sindicoId,
            LocalDateTime dataCriacao
    ) {
        Assembleia a = new Assembleia();
        a.id = id;
        a.titulo = titulo;
        a.dataHora = dataHora;
        a.local = local;
        a.pauta = pauta != null ? pauta : List.of();
        a.status = status;
        a.sindicoId = sindicoId;
        a.dataCriacao = dataCriacao;
        return a;
    }

    public void editar(String titulo, LocalDateTime dataHora, String local, List<String> pauta) {
        if (this.status != StatusAssembleia.AGENDADA) {
            throw new DomainException("Apenas assembleias agendadas podem ser editadas");
        }
        validarAntecedencia(dataHora);
        this.titulo = titulo;
        this.dataHora = dataHora;
        this.local = local;
        this.pauta = pauta != null ? pauta : List.of();
    }

    public void cancelar() {
        if (this.status != StatusAssembleia.AGENDADA) {
            throw new DomainException("Apenas assembleias agendadas podem ser canceladas");
        }
        this.status = StatusAssembleia.CANCELADA;
    }

    public void encerrar() {
        if (this.status != StatusAssembleia.AGENDADA) {
            throw new DomainException("Apenas assembleias agendadas podem ser encerradas");
        }
        if (this.pauta == null || this.pauta.isEmpty()) {
            throw new DomainException("Não é possível encerrar uma assembleia sem pauta");
        }
        this.status = StatusAssembleia.ENCERRADA;
    }

    private static void validarAntecedencia(LocalDateTime dataHora) {
        if (dataHora.isBefore(LocalDateTime.now().plusDays(ANTECEDENCIA_MINIMA_DIAS))) {
            throw new DomainException(
                "A assembleia deve ser agendada com no mínimo " + ANTECEDENCIA_MINIMA_DIAS + " dias de antecedência"
            );
        }
    }
}
