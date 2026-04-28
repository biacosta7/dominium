package br.com.cesar.gestaoCondominial.financeiro.aplicacao.recurso.usecase;

import br.com.cesar.gestaoCondominial.financeiro.aplicacao.recurso.dto.AbrirRecursoRequestDTO;
import br.com.cesar.gestaoCondominial.financeiro.dominio.recurso.Recurso;
import br.com.cesar.gestaoCondominial.financeiro.dominio.recurso.repository.RecursoRepository;
import br.com.cesar.gestaoCondominial.financeiro.dominio.multa.Multa;
import br.com.cesar.gestaoCondominial.financeiro.dominio.multa.MultaId;
import br.com.cesar.gestaoCondominial.financeiro.dominio.multa.repository.MultaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AbrirRecursoUseCase {

    private final RecursoRepository recursoRepository;
    private final MultaRepository multaRepository;

    public UUID execute(AbrirRecursoRequestDTO dto) {
            Multa multa = multaRepository.findById(new MultaId(dto.getMultaId()))
                .orElseThrow(() -> new IllegalArgumentException("Multa não encontrada."));
        if (multa.getDataCriacao() != null && multa.getDataCriacao().plusDays(15).isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Prazo máximo de 15 dias para recurso expirado.");
        }

        multa.setDataContestacao(LocalDateTime.now());
        multa.setJustificativaContestacao(dto.getMotivo());
        multaRepository.save(multa);

        Recurso recurso = Recurso.abrir(new MultaId(dto.getMultaId()), dto.getMoradorId(), dto.getMotivo());
        recursoRepository.salvar(recurso);

        return recurso.getId().getValue();
    }
}