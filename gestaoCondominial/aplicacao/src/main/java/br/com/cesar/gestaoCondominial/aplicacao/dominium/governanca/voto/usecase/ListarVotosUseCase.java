package br.com.cesar.gestaoCondominial.aplicacao.dominium.governanca.voto.usecase;

import br.com.cesar.gestaoCondominial.dominio.dominium.governanca.pauta.Pauta;
import br.com.cesar.gestaoCondominial.dominio.dominium.governanca.pauta.PautaId;
import br.com.cesar.gestaoCondominial.dominio.dominium.governanca.voto.Voto;
import br.com.cesar.gestaoCondominial.dominio.dominium.governanca.voto.VotoRepository;
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
