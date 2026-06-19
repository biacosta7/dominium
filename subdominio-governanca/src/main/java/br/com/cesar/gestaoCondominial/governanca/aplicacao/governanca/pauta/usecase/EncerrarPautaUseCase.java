package br.com.cesar.gestaoCondominial.governanca.aplicacao.governanca.pauta.usecase;

import br.com.cesar.gestaoCondominial.governanca.aplicacao.governanca.service.RegraVotacao;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.pauta.Pauta;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.pauta.PautaId;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.pauta.PautaRepository;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.pauta.ResultadoPauta;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.voto.Voto;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.voto.VotoRepository;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.voto.iterator.VotoCollection;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
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

        List<Voto> votos = votoRepository.buscarPorPauta(pautaId);

        // Pauta sem nenhum voto é encerrada como ADIADO em vez de bloquear o encerramento
        ResultadoPauta resultado = votos.isEmpty()
                ? ResultadoPauta.ADIADO
                : regraVotacao.calcularResultado(pauta, new VotoCollection(votos).iterator());

        pauta.encerrar(resultado);

        return pautaRepository.save(pauta);
    }
}
