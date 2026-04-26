package com.dominium.backend.domain.governanca.pauta;

import com.dominium.backend.domain.governanca.RegraVotacao;
import com.dominium.backend.domain.governanca.voto.Voto;
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
