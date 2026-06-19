package br.com.cesar.gestaoCondominial.governanca.aplicacao.governanca.pauta.usecase;

import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.ResourceNotFoundException;
import br.com.cesar.gestaoCondominial.governanca.dominio.assembleia.AssembleiaId;
import br.com.cesar.gestaoCondominial.governanca.dominio.assembleia.repository.AssembleiaRepository;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.pauta.Pauta;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.pauta.PautaId;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.pauta.PautaRepository;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.pauta.TipoMaioria;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.pauta.TipoQuorum;
import org.springframework.stereotype.Service;

@Service
public class AbrirPautaUseCase {
    private final PautaRepository repository;
    private final AssembleiaRepository assembleiaRepository;

    public AbrirPautaUseCase(PautaRepository repository, AssembleiaRepository assembleiaRepository) {
        this.repository = repository;
        this.assembleiaRepository = assembleiaRepository;
    }

    public Pauta executar(
            AssembleiaId assembleiaId,
            String titulo,
            String descricao,
            TipoQuorum quorum,
            TipoMaioria maioria
    ) {
        assembleiaRepository.findById(assembleiaId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Assembleia não encontrada com id: " + assembleiaId.getValor()));

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
