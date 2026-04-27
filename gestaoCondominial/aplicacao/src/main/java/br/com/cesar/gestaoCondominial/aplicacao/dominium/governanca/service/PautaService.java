package br.com.cesar.gestaoCondominial.aplicacao.dominium.governanca.service;

import br.com.cesar.gestaoCondominial.aplicacao.dominium.governanca.service.RegraVotacao;
import br.com.cesar.gestaoCondominial.dominio.dominium.governanca.pauta.Pauta;
import br.com.cesar.gestaoCondominial.dominio.dominium.governanca.pauta.ResultadoPauta;
import br.com.cesar.gestaoCondominial.dominio.dominium.governanca.voto.Voto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PautaService {

    private final RegraVotacao regraVotacao;

    public PautaService(RegraVotacao regraVotacao) {
        this.regraVotacao = regraVotacao;
    }

    public void encerrarPauta(Pauta pauta, List<Voto> votos) {

        pauta.validarSeEstaAberta();

        regraVotacao.validarQuorum(pauta, votos);

        ResultadoPauta resultado =
                regraVotacao.calcularResultado(pauta, votos);

        pauta.encerrar(resultado);
    }
}
