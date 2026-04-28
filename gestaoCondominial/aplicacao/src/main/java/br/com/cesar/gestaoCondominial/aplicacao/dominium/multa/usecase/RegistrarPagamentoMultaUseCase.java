package br.com.cesar.gestaoCondominial.aplicacao.dominium.multa.usecase;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import br.com.cesar.gestaoCondominial.aplicacao.dominium.multa.dto.MultaResponseDTO;
import br.com.cesar.gestaoCondominial.aplicacao.dominium.multa.dto.RegistrarPagamentoRequestDTO;
import br.com.cesar.gestaoCondominial.dominio.dominium.multa.Multa;
import br.com.cesar.gestaoCondominial.dominio.dominium.multa.MultaId;
import br.com.cesar.gestaoCondominial.dominio.dominium.multa.StatusMulta;
import br.com.cesar.gestaoCondominial.dominio.dominium.multa.repository.MultaRepository;

@Service
public class RegistrarPagamentoMultaUseCase {

    private final MultaRepository multaRepository;

    public RegistrarPagamentoMultaUseCase(
            MultaRepository multaRepository
    ) {
        this.multaRepository = multaRepository;
    }

    public MultaResponseDTO execute(
            Long multaId,
            RegistrarPagamentoRequestDTO request
    ) {
        Multa multa = multaRepository.findById(new MultaId(multaId))
                .orElseThrow(() ->
                        new IllegalArgumentException("Multa não encontrada."));

        if (multa.getStatus() == StatusMulta.PAGA) {
            throw new IllegalStateException(
                    "Esta multa já foi paga."
            );
        }

        multa.setValorPago(request.getValorPago());
        multa.setDataPagamento(LocalDateTime.now());
        multa.setStatus(StatusMulta.PAGA);
        multa.setUpdatedAt(LocalDateTime.now());

        Multa salva = multaRepository.save(multa);

        return MultaResponseDTO.fromEntity(salva);
    }
}