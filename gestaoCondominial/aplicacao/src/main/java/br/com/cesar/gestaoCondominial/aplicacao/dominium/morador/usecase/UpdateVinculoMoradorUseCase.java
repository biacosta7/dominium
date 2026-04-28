package br.com.cesar.gestaoCondominial.aplicacao.dominium.morador.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.cesar.gestaoCondominial.aplicacao.dominium.morador.dto.VinculoRequestDTO;
import br.com.cesar.gestaoCondominial.aplicacao.dominium.morador.dto.VinculoResponseDTO;
import br.com.cesar.gestaoCondominial.dominio.dominium.morador.VinculoMorador;
import br.com.cesar.gestaoCondominial.dominio.dominium.morador.repository.VinculoMoradorRepository;

@Service
public class UpdateVinculoMoradorUseCase {

    private final VinculoMoradorRepository vinculoMoradorRepository;

    public UpdateVinculoMoradorUseCase(VinculoMoradorRepository vinculoMoradorRepository) {
        this.vinculoMoradorRepository = vinculoMoradorRepository;
    }

    @Transactional
    public VinculoResponseDTO execute(Long vinculoId, VinculoRequestDTO request) {
        VinculoMorador vinculo = vinculoMoradorRepository.findById(vinculoId)
                .orElseThrow(() -> new IllegalArgumentException("Vínculo não encontrado"));

        if (request.getTipo() != null) {
            vinculo.setTipo(request.getTipo());
        }

        VinculoMorador updated = vinculoMoradorRepository.save(vinculo);

        return VinculoResponseDTO.fromEntity(updated);
    }
}
