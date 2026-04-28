package br.com.cesar.gestaoCondominial.moradores.aplicacao.morador.usecase;

import java.util.List;
import java.util.stream.Collectors;

import br.com.cesar.gestaoCondominial.moradores.aplicacao.morador.dto.VinculoResponseDTO;
import br.com.cesar.gestaoCondominial.moradores.dominio.morador.StatusVinculo;
import br.com.cesar.gestaoCondominial.moradores.dominio.morador.repository.VinculoMoradorRepository;

import org.springframework.stereotype.Service;

@Service
public class GetVinculosPorUnidadeUseCase {

    private final VinculoMoradorRepository vinculoMoradorRepository;

    public GetVinculosPorUnidadeUseCase(VinculoMoradorRepository vinculoMoradorRepository) {
        this.vinculoMoradorRepository = vinculoMoradorRepository;
    }

    public List<VinculoResponseDTO> execute(Long unidadeId) {
        return vinculoMoradorRepository.findByUnidadeIdAndStatus(unidadeId, StatusVinculo.ATIVO)
                .stream()
                .map(VinculoResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
