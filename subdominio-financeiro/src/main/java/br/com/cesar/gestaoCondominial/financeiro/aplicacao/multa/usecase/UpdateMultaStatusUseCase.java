package br.com.cesar.gestaoCondominial.financeiro.aplicacao.multa.usecase;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import br.com.cesar.gestaoCondominial.financeiro.aplicacao.multa.dto.MultaResponseDTO;
import br.com.cesar.gestaoCondominial.financeiro.aplicacao.multa.dto.UpdateMultaStatusRequestDTO;
import br.com.cesar.gestaoCondominial.financeiro.dominio.multa.Multa;
import br.com.cesar.gestaoCondominial.financeiro.dominio.multa.MultaId;
import br.com.cesar.gestaoCondominial.financeiro.dominio.multa.repository.MultaRepository;

@Service
public class UpdateMultaStatusUseCase {

    private final MultaRepository multaRepository;

    public UpdateMultaStatusUseCase(
            MultaRepository multaRepository
    ) {
        this.multaRepository = multaRepository;
    }

    public MultaResponseDTO execute(
            Long multaId,
            UpdateMultaStatusRequestDTO request
    ) {
        Multa multa = multaRepository.findById(new MultaId(multaId))
                .orElseThrow(() ->
                        new IllegalArgumentException("Multa não encontrada."));

        multa.setStatus(request.getStatus());
        multa.setUpdatedAt(LocalDateTime.now());

        Multa atualizada = multaRepository.save(multa);

        return MultaResponseDTO.fromEntity(atualizada);
    }
}