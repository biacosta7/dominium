package br.com.cesar.gestaoCondominial.aplicacao.dominium.governanca.pauta.usecase;

import br.com.cesar.gestaoCondominial.dominio.dominium.assembleia.AssembleiaId;
import br.com.cesar.gestaoCondominial.dominio.dominium.governanca.pauta.Pauta;
import br.com.cesar.gestaoCondominial.dominio.dominium.governanca.pauta.PautaId;
import br.com.cesar.gestaoCondominial.dominio.dominium.governanca.pauta.PautaRepository;
import br.com.cesar.gestaoCondominial.dominio.dominium.governanca.pauta.TipoMaioria;
import br.com.cesar.gestaoCondominial.dominio.dominium.governanca.pauta.TipoQuorum;
import org.springframework.stereotype.Service;

@Service
public class AbrirPautaUseCase {
    private final PautaRepository repository;

    public AbrirPautaUseCase(PautaRepository repository) {
        this.repository = repository;
    }

    public Pauta executar(
            AssembleiaId assembleiaId,
            String titulo,
            String descricao,
            TipoQuorum quorum,
            TipoMaioria maioria
    ) {

        Pauta pauta = Pauta.criar(
                new PautaId(null),
                assembleiaId,
                titulo,
                descricao,
                quorum,
                maioria
        );

        return repository.save(pauta);
    }
}
