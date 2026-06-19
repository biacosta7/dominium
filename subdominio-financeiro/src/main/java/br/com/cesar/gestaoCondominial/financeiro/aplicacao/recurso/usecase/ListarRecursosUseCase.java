package br.com.cesar.gestaoCondominial.financeiro.aplicacao.recurso.usecase;

import br.com.cesar.gestaoCondominial.financeiro.dominio.recurso.Recurso;
import br.com.cesar.gestaoCondominial.financeiro.dominio.recurso.repository.RecursoRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ListarRecursosUseCase {
    private final RecursoRepository recursoRepository;

    public List<Response> execute() {
        return recursoRepository.listarTodos().stream()
                .map(Response::fromDomain)
                .toList();
    }

    @Data
    public static class Response {
        private UUID id;
        private Long multaId;
        private Long moradorId;
        private String motivo;
        private LocalDateTime dataSolicitacao;
        private String status;
        private String justificativaSindico;
        private LocalDateTime dataDecisao;

        private static Response fromDomain(Recurso recurso) {
            Response dto = new Response();
            dto.setId(recurso.getId().getValue());
            dto.setMultaId(recurso.getMultaId().getValor());
            dto.setMoradorId(recurso.getMoradorId());
            dto.setMotivo(recurso.getMotivo());
            dto.setDataSolicitacao(recurso.getDataSolicitacao());
            dto.setStatus(recurso.getStatus().name());
            dto.setJustificativaSindico(recurso.getJustificativaSindico());
            dto.setDataDecisao(recurso.getDataDecisao());
            return dto;
        }
    }
}
