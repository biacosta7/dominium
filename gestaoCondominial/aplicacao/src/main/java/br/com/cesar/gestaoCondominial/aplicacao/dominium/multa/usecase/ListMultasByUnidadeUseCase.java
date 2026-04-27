package br.com.cesar.gestaoCondominial.aplicacao.dominium.multa.usecase;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.cesar.gestaoCondominial.aplicacao.dominium.multa.dto.MultaResponseDTO;
import br.com.cesar.gestaoCondominial.dominio.dominium.multa.repository.MultaRepository;
import br.com.cesar.gestaoCondominial.dominio.dominium.unidade.UnidadeId;
import br.com.cesar.gestaoCondominial.dominio.dominium.unidade.repository.UnidadeRepository;

@Service
public class ListMultasByUnidadeUseCase {

    private final MultaRepository multaRepository;
    private final UnidadeRepository unidadeRepository;

    public ListMultasByUnidadeUseCase(
            MultaRepository multaRepository,
            UnidadeRepository unidadeRepository
    ) {
        this.multaRepository = multaRepository;
        this.unidadeRepository = unidadeRepository;
    }

    public List<MultaResponseDTO> execute(Long unidadeId) {

        unidadeRepository.findById(new UnidadeId(unidadeId))
                .orElseThrow(() ->
                        new IllegalArgumentException("Unidade não encontrada."));

        return multaRepository.findByUnidadeId(new UnidadeId(unidadeId))
                .stream()
                .map(MultaResponseDTO::fromEntity)
                .toList();
    }
}