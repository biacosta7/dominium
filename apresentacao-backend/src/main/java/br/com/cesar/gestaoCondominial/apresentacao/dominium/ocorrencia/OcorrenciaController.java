package br.com.cesar.gestaoCondominial.apresentacao.dominium.ocorrencia;

import br.com.cesar.gestaoCondominial.operacional.aplicacao.ocorrencia.dto.AtualizarStatusOcorrenciaRequestDTO;
import br.com.cesar.gestaoCondominial.operacional.aplicacao.ocorrencia.dto.EncerrarOcorrenciaRequestDTO;
import br.com.cesar.gestaoCondominial.operacional.aplicacao.ocorrencia.dto.OcorrenciaRequestDTO;
import br.com.cesar.gestaoCondominial.operacional.aplicacao.ocorrencia.dto.OcorrenciaResponseDTO;
import br.com.cesar.gestaoCondominial.operacional.aplicacao.ocorrencia.usecase.AtualizarStatusOcorrenciaUseCase;
import br.com.cesar.gestaoCondominial.operacional.aplicacao.ocorrencia.usecase.EncerrarOcorrenciaUseCase;
import br.com.cesar.gestaoCondominial.operacional.aplicacao.ocorrencia.usecase.GerenciarOcorrenciaUseCase;
import br.com.cesar.gestaoCondominial.operacional.dominio.ocorrencia.Ocorrencia;
import br.com.cesar.gestaoCondominial.operacional.dominio.ocorrencia.repository.OcorrenciaRepository;
import br.com.cesar.gestaoCondominial.operacional.dominio.ocorrencia.repository.TipoOcorrenciaRepository;
import br.com.cesar.gestaoCondominial.financeiro.dominio.multa.repository.MultaRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ocorrencias")
public class OcorrenciaController {

    private final GerenciarOcorrenciaUseCase gerenciarOcorrenciaUseCase;
    private final AtualizarStatusOcorrenciaUseCase atualizarStatusOcorrenciaUseCase;
    private final EncerrarOcorrenciaUseCase encerrarOcorrenciaUseCase;
    private final OcorrenciaRepository ocorrenciaRepository;
    private final MultaRepository multaRepository;
    private final TipoOcorrenciaRepository tipoOcorrenciaRepository;

    public OcorrenciaController(
            GerenciarOcorrenciaUseCase gerenciarOcorrenciaUseCase,
            AtualizarStatusOcorrenciaUseCase atualizarStatusOcorrenciaUseCase,
            EncerrarOcorrenciaUseCase encerrarOcorrenciaUseCase,
            OcorrenciaRepository ocorrenciaRepository,
            MultaRepository multaRepository,
            TipoOcorrenciaRepository tipoOcorrenciaRepository) {
        this.gerenciarOcorrenciaUseCase = gerenciarOcorrenciaUseCase;
        this.atualizarStatusOcorrenciaUseCase = atualizarStatusOcorrenciaUseCase;
        this.encerrarOcorrenciaUseCase = encerrarOcorrenciaUseCase;
        this.ocorrenciaRepository = ocorrenciaRepository;
        this.multaRepository = multaRepository;
        this.tipoOcorrenciaRepository = tipoOcorrenciaRepository;
    }

    @GetMapping
    public ResponseEntity<List<OcorrenciaResponseDTO>> listarTodas() {
        List<Ocorrencia> ocorrencias = ocorrenciaRepository.listarTodas();
        List<OcorrenciaResponseDTO> dtos = ocorrencias.stream()
                .map(this::mapToResponseDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/unidade/{unidadeId}")
    public ResponseEntity<List<OcorrenciaResponseDTO>> listarPorUnidade(@PathVariable Long unidadeId) {
        List<OcorrenciaResponseDTO> dtos = ocorrenciaRepository.listarPorUnidade(unidadeId).stream()
                .map(this::mapToResponseDTO)
                .toList();
        return ResponseEntity.ok(dtos);
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
        dto.setTipo(ocorrencia.getTipo());
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

        if (ocorrencia.getId() != null) {
            multaRepository.findByOcorrenciaId(ocorrencia.getId())
                    .stream()
                    .findFirst()
                    .ifPresentOrElse(
                            m -> dto.setValorMulta(m.getValor()),
                            () -> {
                                if (ocorrencia.getTipo() != null) {
                                    tipoOcorrenciaRepository.findByNome(ocorrencia.getTipo())
                                            .ifPresent(t -> dto.setValorMulta(t.getValorBaseMulta()));
                                }
                            }
                    );
        }

        return dto;
    }
}
