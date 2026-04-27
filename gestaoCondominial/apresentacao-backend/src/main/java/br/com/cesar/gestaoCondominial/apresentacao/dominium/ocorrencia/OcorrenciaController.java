package br.com.cesar.gestaoCondominial.apresentacao.dominium.ocorrencia;

import br.com.cesar.gestaoCondominial.aplicacao.dominium.ocorrencia.dto.AtualizarStatusOcorrenciaRequestDTO;
import br.com.cesar.gestaoCondominial.aplicacao.dominium.ocorrencia.dto.EncerrarOcorrenciaRequestDTO;
import br.com.cesar.gestaoCondominial.aplicacao.dominium.ocorrencia.dto.OcorrenciaRequestDTO;
import br.com.cesar.gestaoCondominial.aplicacao.dominium.ocorrencia.dto.OcorrenciaResponseDTO;
import br.com.cesar.gestaoCondominial.aplicacao.dominium.ocorrencia.usecase.AtualizarStatusOcorrenciaUseCase;
import br.com.cesar.gestaoCondominial.aplicacao.dominium.ocorrencia.usecase.EncerrarOcorrenciaUseCase;
import br.com.cesar.gestaoCondominial.aplicacao.dominium.ocorrencia.usecase.GerenciarOcorrenciaUseCase;
import br.com.cesar.gestaoCondominial.dominio.dominium.ocorrencia.Ocorrencia;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ocorrencias")
public class OcorrenciaController {

    private final GerenciarOcorrenciaUseCase gerenciarOcorrenciaUseCase;
    private final AtualizarStatusOcorrenciaUseCase atualizarStatusOcorrenciaUseCase;
    private final EncerrarOcorrenciaUseCase encerrarOcorrenciaUseCase;

    public OcorrenciaController(
            GerenciarOcorrenciaUseCase gerenciarOcorrenciaUseCase,
            AtualizarStatusOcorrenciaUseCase atualizarStatusOcorrenciaUseCase,
            EncerrarOcorrenciaUseCase encerrarOcorrenciaUseCase) {
        this.gerenciarOcorrenciaUseCase = gerenciarOcorrenciaUseCase;
        this.atualizarStatusOcorrenciaUseCase = atualizarStatusOcorrenciaUseCase;
        this.encerrarOcorrenciaUseCase = encerrarOcorrenciaUseCase;
    }

    @PostMapping
    public ResponseEntity<OcorrenciaResponseDTO> criar(@Valid @RequestBody OcorrenciaRequestDTO request) {
        Ocorrencia ocorrencia = gerenciarOcorrenciaUseCase.executar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponseDTO(ocorrencia));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OcorrenciaResponseDTO> atualizarStatus(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarStatusOcorrenciaRequestDTO request) {
        Ocorrencia ocorrencia = atualizarStatusOcorrenciaUseCase.executar(id, request.getStatus());
        return ResponseEntity.ok(mapToResponseDTO(ocorrencia));
    }

    @PostMapping("/{id}/encerrar")
    public ResponseEntity<OcorrenciaResponseDTO> encerrar(
            @PathVariable Long id,
            @Valid @RequestBody EncerrarOcorrenciaRequestDTO request) {
        Ocorrencia ocorrencia = encerrarOcorrenciaUseCase.executar(
                id,
                request.getPenalidade(),
                request.getObservacao(),
                request.getValorMulta()
        );
        return ResponseEntity.ok(mapToResponseDTO(ocorrencia));
    }

    private OcorrenciaResponseDTO mapToResponseDTO(Ocorrencia ocorrencia) {
        OcorrenciaResponseDTO dto = new OcorrenciaResponseDTO();
        dto.setId(ocorrencia.getId());
        dto.setDescricao(ocorrencia.getDescricao());
        if (ocorrencia.getUnidadeId() != null) {
            dto.setUnidadeId(ocorrencia.getUnidadeId().getValor());
        }
        if (ocorrencia.getRelator() != null) {
            dto.setRelatorId(ocorrencia.getRelator().getId());
            dto.setRelatorNome(ocorrencia.getRelator().getNome());
        }
        dto.setDataRegistro(ocorrencia.getDataRegistro());
        if (ocorrencia.getStatus() != null) {
            dto.setStatus(ocorrencia.getStatus().name());
        }
        if (ocorrencia.getPenalidade() != null) {
            dto.setPenalidade(ocorrencia.getPenalidade().name());
        }
        dto.setObservacaoSindico(ocorrencia.getObservacaoSindico());
        return dto;
    }
}
