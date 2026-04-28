package br.com.cesar.gestaoCondominial.governanca.aplicacao.governanca.voto.usecase;

import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.pauta.Pauta;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.pauta.PautaId;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.voto.Voto;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.voto.VotoRepository;
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
