package com.dominium.backend.application.governanca.pauta.usecase;

import com.dominium.backend.domain.governanca.RegraVotacao;
import com.dominium.backend.domain.governanca.pauta.Pauta;
import com.dominium.backend.domain.governanca.pauta.PautaId;
import com.dominium.backend.domain.governanca.pauta.PautaRepository;
import com.dominium.backend.domain.governanca.pauta.ResultadoPauta;
import com.dominium.backend.domain.governanca.voto.Voto;
import com.dominium.backend.domain.governanca.voto.VotoRepository;

import java.util.List;

public class EncerrarPautaUseCase {
    private final PautaRepository pautaRepository;
    private final VotoRepository votoRepository;
    private final RegraVotacao regraVotacao;

    public EncerrarPautaUseCase(
            PautaRepository pautaRepository,
            VotoRepository votoRepository,
            RegraVotacao regraVotacao
    ) {
        this.pautaRepository = pautaRepository;
        this.votoRepository = votoRepository;
        this.regraVotacao = regraVotacao;
    }

    public Pauta executar(PautaId pautaId) {

        Pauta pauta = pautaRepository.findById(pautaId)
                .orElseThrow(() -> new RuntimeException("Pauta não encontrada"));

        List<Voto> votos = votoRepository.findByPauta(pautaId);

        ResultadoPauta resultado =
                regraVotacao.calcularResultado(pauta, votos);

        pauta.encerrar(resultado);

        return pautaRepository.save(pauta);
    }
}
