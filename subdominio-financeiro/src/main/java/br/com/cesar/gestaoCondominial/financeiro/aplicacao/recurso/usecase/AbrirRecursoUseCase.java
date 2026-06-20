package br.com.cesar.gestaoCondominial.financeiro.aplicacao.recurso.usecase;

import br.com.cesar.gestaoCondominial.financeiro.aplicacao.recurso.dto.AbrirRecursoRequestDTO;
import br.com.cesar.gestaoCondominial.financeiro.aplicacao.recurso.decorator.RecursoDecorator;
import br.com.cesar.gestaoCondominial.financeiro.dominio.recurso.Recurso;
import br.com.cesar.gestaoCondominial.financeiro.dominio.recurso.repository.RecursoRepository;
import br.com.cesar.gestaoCondominial.financeiro.dominio.multa.Multa;
import br.com.cesar.gestaoCondominial.financeiro.dominio.multa.MultaId;
import br.com.cesar.gestaoCondominial.financeiro.dominio.multa.StatusMulta;
import br.com.cesar.gestaoCondominial.financeiro.dominio.multa.repository.MultaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AbrirRecursoUseCase {

    private final RecursoRepository recursoRepository;
    private final MultaRepository multaRepository;

    public UUID execute(AbrirRecursoRequestDTO dto) {
        Multa multa = multaRepository.findById(new MultaId(dto.getMultaId()))
                .orElseThrow(() -> new IllegalArgumentException("Multa não encontrada."));

        RecursoDecorator.Abertura abertura = (request, multaValidada) -> {
            multaValidada.setStatus(StatusMulta.CONTESTADA);
            multaValidada.setDataContestacao(java.time.LocalDateTime.now());
            multaValidada.setJustificativaContestacao(request.getMotivo());
            multaRepository.save(multaValidada);

            Long moradorId = request.getUsuarioId() != null ? request.getUsuarioId() : 1L;
            Recurso recurso = Recurso.abrir(
                    new MultaId(request.getMultaId()), moradorId, request.getMotivo());
            recursoRepository.salvar(recurso);
            return recurso.getId().getValue();
        };

        abertura = new RecursoDecorator.Validacao(abertura);
        return abertura.abrir(dto, multa);
    }
}
