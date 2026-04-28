package br.com.cesar.gestaoCondominial.governanca.aplicacao.governanca.voto.usecase;

import br.com.cesar.gestaoCondominial.governanca.aplicacao.governanca.service.RegraVotacao;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.pauta.Pauta;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.pauta.PautaId;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.pauta.PautaRepository;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.voto.OpcaoVoto;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.voto.Voto;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.voto.VotoId;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.voto.VotoRepository;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.UnidadeId;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.UsuarioId;
import org.springframework.stereotype.Service;

@Service
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
