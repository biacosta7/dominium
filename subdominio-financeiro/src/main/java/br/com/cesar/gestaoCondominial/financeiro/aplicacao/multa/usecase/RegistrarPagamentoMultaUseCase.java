package br.com.cesar.gestaoCondominial.financeiro.aplicacao.multa.usecase;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import br.com.cesar.gestaoCondominial.financeiro.aplicacao.multa.dto.MultaResponseDTO;
import br.com.cesar.gestaoCondominial.financeiro.aplicacao.multa.dto.RegistrarPagamentoRequestDTO;
import br.com.cesar.gestaoCondominial.financeiro.dominio.multa.Multa;
import br.com.cesar.gestaoCondominial.financeiro.dominio.multa.MultaId;
import br.com.cesar.gestaoCondominial.financeiro.dominio.multa.StatusMulta;
import br.com.cesar.gestaoCondominial.financeiro.dominio.multa.repository.MultaRepository;

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