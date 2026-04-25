package com.dominium.backend.application.governanca.pauta.usecase;

import com.dominium.backend.domain.governanca.pauta.*;

public class AbrirPautaUseCase {
    private final PautaRepository repository;

    public AbrirPautaUseCase(PautaRepository repository) {
        this.repository = repository;
    }

    public Pauta executar(
            AssembleiaId assembleiaId,
            String titulo,
            String descricao,
            TipoQuorum quorum,
            TipoMaioria maioria
    ) {

        Pauta pauta = Pauta.criar(
                PautaId.novo(),
                assembleiaId,
                titulo,
                descricao,
                quorum,
                maioria
        );

        return repository.save(pauta);
    }
}
