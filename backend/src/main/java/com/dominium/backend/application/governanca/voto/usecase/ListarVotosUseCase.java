package com.dominium.backend.application.governanca.voto.usecase;

import com.dominium.backend.domain.governanca.pauta.Pauta;
import com.dominium.backend.domain.governanca.pauta.PautaId;
import com.dominium.backend.domain.governanca.voto.Voto;
import com.dominium.backend.domain.governanca.voto.VotoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarVotosUseCase {

    private final VotoRepository votoRepository;

    public ListarVotosUseCase(VotoRepository votoRepository){
        this.votoRepository = votoRepository;
    }

    public List<Voto> ListarPorPauta(PautaId pautaId) {
        return votoRepository.buscarPorPauta(pautaId);
    }
}
