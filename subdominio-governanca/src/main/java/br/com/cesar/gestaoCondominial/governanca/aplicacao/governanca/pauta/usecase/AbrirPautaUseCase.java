package br.com.cesar.gestaoCondominial.governanca.aplicacao.governanca.pauta.usecase;

import br.com.cesar.gestaoCondominial.governanca.dominio.assembleia.AssembleiaId;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.pauta.Pauta;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.pauta.PautaId;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.pauta.PautaRepository;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.pauta.TipoMaioria;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.pauta.TipoQuorum;
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
