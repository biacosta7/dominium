package br.com.cesar.gestaoCondominial.aplicacao.dominium.unidade.usecase;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import br.com.cesar.gestaoCondominial.aplicacao.dominium.unidade.dto.UnidadeResponseDTO;
import br.com.cesar.gestaoCondominial.dominio.dominium.unidade.Unidade;
import br.com.cesar.gestaoCondominial.dominio.dominium.unidade.UnidadeId;
import br.com.cesar.gestaoCondominial.dominio.dominium.unidade.repository.UnidadeRepository;
import java.util.List;

@Service
public class GetUnidadeUseCase {
    private final UnidadeRepository unidadeRepository;

    public GetUnidadeUseCase(UnidadeRepository unidadeRepository) {
        this.unidadeRepository = unidadeRepository;
    }
    public List<UnidadeResponseDTO> findAll(){
        return unidadeRepository.findAll().stream()
        .map(UnidadeResponseDTO::fromEntity)
        .collect(Collectors.toList());
    }

    public UnidadeResponseDTO findById(Long id){

        Unidade unidade = unidadeRepository.findById(new UnidadeId(id))
                .orElseThrow(() -> new IllegalArgumentException("Unidade não encontrada"));
        return UnidadeResponseDTO.fromEntity(unidade);
    }
}