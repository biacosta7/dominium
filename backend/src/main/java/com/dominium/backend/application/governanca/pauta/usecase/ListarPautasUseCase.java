package com.dominium.backend.application.governanca.pauta.usecase;

import com.dominium.backend.domain.governanca.pauta.Pauta;
import com.dominium.backend.domain.governanca.pauta.PautaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarPautasUseCase {
    private final PautaRepository repository;

    public ListarPautasUseCase(PautaRepository repository) {
        this.repository = repository;
    }

    public List<Pauta> executar() {
        return repository.buscarAbertas();
    }
}
