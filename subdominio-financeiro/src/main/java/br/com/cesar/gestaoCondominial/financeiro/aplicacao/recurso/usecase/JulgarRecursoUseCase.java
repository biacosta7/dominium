package br.com.cesar.gestaoCondominial.financeiro.aplicacao.recurso.usecase;

import br.com.cesar.gestaoCondominial.financeiro.aplicacao.recurso.dto.JulgarRecursoRequestDTO;
import br.com.cesar.gestaoCondominial.financeiro.dominio.recurso.Recurso;
import br.com.cesar.gestaoCondominial.financeiro.dominio.recurso.RecursoId;
import br.com.cesar.gestaoCondominial.financeiro.dominio.recurso.StatusRecurso;
import br.com.cesar.gestaoCondominial.financeiro.dominio.recurso.repository.RecursoRepository;
import br.com.cesar.gestaoCondominial.financeiro.dominio.multa.Multa;
import br.com.cesar.gestaoCondominial.financeiro.dominio.multa.StatusMulta;
import br.com.cesar.gestaoCondominial.financeiro.dominio.multa.repository.MultaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class JulgarRecursoUseCase {

    private final RecursoRepository recursoRepository;
    private final MultaRepository multaRepository;

    public void execute(UUID recursoId, JulgarRecursoRequestDTO dto) {
        Recurso recurso = recursoRepository.buscarPorId(new RecursoId(recursoId))
                .orElseThrow(() -> new IllegalArgumentException("Recurso não encontrado."));

        recurso.julgar(dto.getStatus(), dto.getJustificativa());
        recursoRepository.atualizar(recurso);

        Multa multa = multaRepository.findById(recurso.getMultaId())
                .orElseThrow(() -> new IllegalArgumentException("Multa não encontrada."));

        if (dto.getStatus() == StatusRecurso.DEFERIDO) {
            if (dto.isCancelarMulta()) {
                multa.setValor(BigDecimal.ZERO);
                multa.setStatus(StatusMulta.CANCELADA);
            } else if (dto.getNovoValorMulta() != null) {
                multa.setValor(dto.getNovoValorMulta());
                multa.setStatus(StatusMulta.ABERTA);
            }
        } else {
            multa.setStatus(StatusMulta.RECURSO_INDEFERIDO);
        }
        multaRepository.save(multa);
    }
}
