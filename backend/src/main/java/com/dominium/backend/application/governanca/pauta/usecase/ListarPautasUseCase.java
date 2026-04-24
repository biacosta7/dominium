package com.dominium.backend.application.governanca.pauta.usecase;

import com.dominium.backend.domain.governanca.pauta.Pauta;
import com.dominium.backend.domain.governanca.pauta.PautaId;
import com.dominium.backend.domain.governanca.pauta.PautaRepository;

import java.util.List;

public class ListarPautasUseCase {
    private final PautaRepository repository;

    public ListarPautasUseCase(PautaRepository repository) {
        this.repository = repository;
    }

    public List<Pauta> executar() {
        return repository.buscarAbertas();
    }
}
