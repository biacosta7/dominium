package com.dominium.backend.application.assembleia.usecase;

import com.dominium.backend.application.assembleia.dto.VotoRequestDTO;
import com.dominium.backend.domain.assembleia.Assembleia;
import com.dominium.backend.domain.assembleia.AssembleiaId;
import com.dominium.backend.domain.assembleia.Pauta;
import com.dominium.backend.domain.assembleia.TipoVoto;
import com.dominium.backend.domain.assembleia.repository.AssembleiaRepository;
import com.dominium.backend.domain.shared.exceptions.DomainException;
import com.dominium.backend.domain.unidade.StatusAdimplencia;
import com.dominium.backend.domain.unidade.Unidade;
import com.dominium.backend.domain.unidade.repository.UnidadeRepository;
import org.springframework.stereotype.Service;

@Service
public class VotarPautaUseCase {

    private final AssembleiaRepository assembleiaRepository;
    private final UnidadeRepository unidadeRepository;

    public VotarPautaUseCase(AssembleiaRepository assembleiaRepository, UnidadeRepository unidadeRepository) {
        this.assembleiaRepository = assembleiaRepository;
        this.unidadeRepository = unidadeRepository;
    }

    public void executar(String assembleiaId, String pautaId, VotoRequestDTO request) {

        Long idUnidade = Long.valueOf(String.valueOf(request.getUnidadeId()));

        Unidade unidade = unidadeRepository.findById(idUnidade)
                .orElseThrow(() -> new DomainException("Unidade não encontrada."));

        if (unidade.getStatus() != StatusAdimplencia.ADIMPLENTE) {
            throw new DomainException("Apenas unidades adimplentes podem votar.");
        }

        Assembleia assembleia = assembleiaRepository.buscarPorId(new AssembleiaId(assembleiaId))
                .orElseThrow(() -> new DomainException("Assembleia não encontrada."));

        if (assembleia.isConcluida()) {
            throw new DomainException("Assembleia já foi concluída.");
        }

        Pauta pauta = assembleia.getPautas().stream()
                .filter(p -> p.getId().equals(pautaId))
                .findFirst()
                .orElseThrow(() -> new DomainException("Pauta não encontrada nesta assembleia."));

        String unidadeIdStr = String.valueOf(idUnidade);

        // Separei a conversão para facilitar a leitura da IDE
        TipoVoto tipoDoVoto = TipoVoto.valueOf(request.getTipoVoto().toUpperCase());

        pauta.adicionarVoto(unidadeIdStr, tipoDoVoto);

        assembleiaRepository.registrarVoto(pauta, unidadeIdStr, tipoDoVoto.name());
    }
}