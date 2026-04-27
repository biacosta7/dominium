package com.dominium.backend.domain.recurso;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;
import com.dominium.backend.domain.multa.MultaId;
@Getter
@AllArgsConstructor
public class Recurso {
    private RecursoId id;
    private MultaId multaId;
    private UUID moradorId;
    private String motivo;
    private LocalDateTime dataSolicitacao;
    private StatusRecurso status;
    private String justificativaSindico;
    private LocalDateTime dataDecisao;

    public static Recurso abrir(MultaId multaId, UUID moradorId, String motivo) {
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new IllegalArgumentException("O motivo do recurso é obrigatório.");
        }
        return new Recurso(
                new RecursoId(UUID.randomUUID()),
                multaId,
                moradorId,
                motivo,
                LocalDateTime.now(),
                StatusRecurso.PENDENTE,
                null,
                null
        );
    }

    public void julgar(StatusRecurso novoStatus, String justificativa) {
        if (novoStatus == StatusRecurso.PENDENTE) {
            throw new IllegalArgumentException("Status de julgamento inválido.");
        }
        if (justificativa == null || justificativa.trim().isEmpty()) {
            throw new IllegalArgumentException("O síndico deve justificar a decisão do recurso.");
        }

        this.status = novoStatus;
        this.justificativaSindico = justificativa;
        this.dataDecisao = LocalDateTime.now();
    }
}