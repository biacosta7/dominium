package com.dominium.backend.application.governanca.voto.usecase;

import com.dominium.backend.domain.governanca.pauta.Pauta;
import com.dominium.backend.domain.governanca.pauta.PautaId;
import com.dominium.backend.domain.governanca.voto.Voto;
import com.dominium.backend.domain.governanca.voto.VotoRepository;

import java.util.List;

public class ListarVotosUseCase {

    private final VotoRepository votoRepository;

    public ListarVotosUseCase(VotoRepository votoRepository){
        this.votoRepository = votoRepository;
    }

    public List<Voto> listarPorPauta(PautaId pautaId) {
        return votoRepository.findByPauta(pautaId);
    }
}
