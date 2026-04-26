package com.dominium.backend.domain.assembleia;

import com.dominium.backend.domain.shared.exceptions.DomainException;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class Assembleia {
    private AssembleiaId id;
    private LocalDateTime dataHora;
    private String local;
    private List<Pauta> pautas;
    private String ata;
    private boolean concluida;

    public void validar() {
        if (pautas == null || pautas.isEmpty()) {
            throw new DomainException("Uma assembleia precisa de pelo menos uma pauta.");
        }
        if (dataHora.isBefore(LocalDateTime.now())) {
            throw new DomainException("Não é possível agendar assembleias no passado.");
        }
    }

    public void registrarAta(String texto) {
        this.ata = texto;
        this.concluida = true;
    }
}