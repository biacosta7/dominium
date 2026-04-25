package com.dominium.backend.application.assembleia.usecase;

import com.dominium.backend.application.assembleia.dto.AssembleiaRequestDTO;
import com.dominium.backend.application.assembleia.dto.AssembleiaResponseDTO;
import com.dominium.backend.domain.assembleia.Assembleia;
import com.dominium.backend.domain.assembleia.AssembleiaId;
import com.dominium.backend.domain.assembleia.Pauta;
import com.dominium.backend.domain.assembleia.repository.AssembleiaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CriarAssembleiaUseCase {

    private final AssembleiaRepository assembleiaRepository;

    public CriarAssembleiaUseCase(AssembleiaRepository assembleiaRepository) {
        this.assembleiaRepository = assembleiaRepository;
    }

    public AssembleiaResponseDTO executar(AssembleiaRequestDTO request) {
        List<Pauta> pautas = request.getPautas().stream()
                .map(p -> Pauta.nova(p.getTitulo(), p.getDescricao()))
                .collect(Collectors.toList());

        Assembleia assembleia = Assembleia.builder()
                .id(AssembleiaId.gerar())
                .dataHora(request.getDataHora())
                .local(request.getLocal())
                .pautas(pautas)
                .concluida(false)
                .build();

        assembleia.validar();

        assembleiaRepository.salvar(assembleia);

        return AssembleiaResponseDTO.builder()
                .id(assembleia.getId().getId())
                .dataHora(assembleia.getDataHora())
                .local(assembleia.getLocal())
                .concluida(assembleia.isConcluida())
                .build();
    }
}