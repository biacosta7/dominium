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

        Pauta pauta = new Pauta(
                PautaId.novo(),
                assembleiaId,
                titulo,
                descricao,
                quorum,
                maioria,
                StatusPauta.ABERTA,
                ResultadoPauta.EM_ANDAMENTO
        );

        return repository.save(pauta);
    }
}
