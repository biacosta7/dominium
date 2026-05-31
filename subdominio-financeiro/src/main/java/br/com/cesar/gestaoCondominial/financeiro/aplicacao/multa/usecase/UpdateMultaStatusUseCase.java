package br.com.cesar.gestaoCondominial.financeiro.aplicacao.multa.usecase;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import br.com.cesar.gestaoCondominial.financeiro.aplicacao.multa.dto.MultaResponseDTO;
import br.com.cesar.gestaoCondominial.financeiro.aplicacao.multa.dto.UpdateMultaStatusRequestDTO;
import br.com.cesar.gestaoCondominial.financeiro.dominio.multa.Multa;
import br.com.cesar.gestaoCondominial.financeiro.dominio.multa.MultaEventPublisher;
import br.com.cesar.gestaoCondominial.financeiro.dominio.multa.MultaId;
import br.com.cesar.gestaoCondominial.financeiro.dominio.multa.StatusMulta;
import br.com.cesar.gestaoCondominial.financeiro.dominio.multa.repository.MultaRepository;

@Service
public class UpdateMultaStatusUseCase {

    private final MultaRepository multaRepository;
    private final MultaEventPublisher eventPublisher;

    public UpdateMultaStatusUseCase(
            MultaRepository multaRepository,
            MultaEventPublisher eventPublisher
    ) {
        this.multaRepository = multaRepository;
        this.eventPublisher = eventPublisher;
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

        if (atualizada.getStatus() == StatusMulta.CANCELADA) {
            eventPublisher.publicarMultaCancelada(atualizada);
        }

        return MultaResponseDTO.fromEntity(atualizada);
    }
}