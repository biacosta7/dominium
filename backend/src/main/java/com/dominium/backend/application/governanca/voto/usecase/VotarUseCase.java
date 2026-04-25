package com.dominium.backend.application.governanca.voto.usecase;

import com.dominium.backend.domain.governanca.RegraVotacao;
import com.dominium.backend.domain.governanca.pauta.Pauta;
import com.dominium.backend.domain.governanca.pauta.PautaId;
import com.dominium.backend.domain.governanca.pauta.PautaRepository;
import com.dominium.backend.domain.governanca.voto.OpcaoVoto;
import com.dominium.backend.domain.governanca.voto.Voto;
import com.dominium.backend.domain.governanca.voto.VotoId;
import com.dominium.backend.domain.governanca.voto.VotoRepository;
import com.dominium.backend.domain.unidade.UnidadeId;
import com.dominium.backend.domain.usuario.UsuarioId;

public class VotarUseCase {

    private final VotoRepository votoRepository;
    private final PautaRepository pautaRepository;
    private final RegraVotacao regraVotacao;

    public VotarUseCase( VotoRepository votoRepository, PautaRepository pautaRepository,
            RegraVotacao regraVotacao
    ) {
        this.votoRepository = votoRepository;
        this.pautaRepository = pautaRepository;
        this.regraVotacao = regraVotacao;
    }

    public void executar(
            PautaId pautaId,
            UsuarioId usuarioId,
            UnidadeId unidadeId,
            OpcaoVoto opcaoVoto
    ){
        Pauta pauta = pautaRepository.findById(pautaId)
                .orElseThrow(() -> new RuntimeException("Pauta não encontrada"));

        pauta.validarSeEstaAberta();

        regraVotacao.validarElegebilidade(usuarioId, unidadeId);

        boolean jaVotou = votoRepository.findByPautaAndUnidade(pautaId, unidadeId);

        if (jaVotou){
            throw new RuntimeException("Unidade já votou nessa pauta");
        }


        Voto voto = Voto.criar(
                new VotoId(null),
                pautaId,
                unidadeId,
                usuarioId,
                opcaoVoto
        );

        if (!voto.pertenceAPauta(pautaId)) {
            throw new IllegalStateException("Voto não pertence à pauta informada");
        }

        if (!voto.ehDaUnidade(unidadeId)) {
            throw new IllegalStateException("Voto não pertence à unidade informada");
        }

        votoRepository.save(voto);
    }
}
