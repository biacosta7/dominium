package com.dominium.backend.presentation.morador;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import org.springframework.web.bind.annotation.RestController;

import com.dominium.backend.application.morador.dto.VinculoRequestDTO;
import com.dominium.backend.application.morador.dto.VinculoResponseDTO;
import br.com.cesar.gestaoCondominial.apresentacao.dominium.exception.ExceptionHandler;
import com.dominium.backend.application.morador.usecase.CreateVinculoMoradorUseCase;
import com.dominium.backend.application.morador.usecase.EndVinculoMoradorUseCase;
import com.dominium.backend.application.morador.usecase.UpdateVinculoMoradorUseCase;
import com.dominium.backend.application.morador.usecase.GetVinculosPorUnidadeUseCase;

@RestController
@RequestMapping("/api")
public class VinculoMoradorController {

    private final CreateVinculoMoradorUseCase createVinculoMoradorUseCase;
    private final UpdateVinculoMoradorUseCase updateVinculoMoradorUseCase;
    private final EndVinculoMoradorUseCase endVinculoMoradorUseCase;
    private final GetVinculosPorUnidadeUseCase getVinculosPorUnidadeUseCase;
    private final ExceptionHandler exceptionHandler;

    public VinculoMoradorController(
            CreateVinculoMoradorUseCase createVinculoMoradorUseCase,
            UpdateVinculoMoradorUseCase updateVinculoMoradorUseCase,
            EndVinculoMoradorUseCase endVinculoMoradorUseCase,
            GetVinculosPorUnidadeUseCase getVinculosPorUnidadeUseCase,
            ExceptionHandler exceptionHandler) {
        this.createVinculoMoradorUseCase = createVinculoMoradorUseCase;
        this.updateVinculoMoradorUseCase = updateVinculoMoradorUseCase;
        this.endVinculoMoradorUseCase = endVinculoMoradorUseCase;
        this.getVinculosPorUnidadeUseCase = getVinculosPorUnidadeUseCase;
        this.exceptionHandler = exceptionHandler;
    }

    @PostMapping("/unidades/{unidadeId}/moradores")
    public ResponseEntity<?> addMorador(
            @PathVariable Long unidadeId,
            @RequestBody VinculoRequestDTO request) {
        return exceptionHandler.withHandler(() -> {
            VinculoResponseDTO response = createVinculoMoradorUseCase.execute(unidadeId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        });
    }

    @GetMapping("/unidades/{unidadeId}/moradores")
    public ResponseEntity<?> getMoradoresDaUnidade(@PathVariable Long unidadeId) {
        return exceptionHandler.withHandler(() -> {
            List<VinculoResponseDTO> response = getVinculosPorUnidadeUseCase.execute(unidadeId);
            return ResponseEntity.ok(response);
        });
    }

    @PutMapping("/moradores/{vinculoId}")
    public ResponseEntity<?> updateMorador(
            @PathVariable Long vinculoId,
            @RequestBody VinculoRequestDTO request) {
        return exceptionHandler.withHandler(() -> {
            VinculoResponseDTO response = updateVinculoMoradorUseCase.execute(vinculoId, request);
            return ResponseEntity.ok(response);
        });
    }

    @DeleteMapping("/moradores/{vinculoId}")
    public ResponseEntity<?> removeMorador(
            @PathVariable Long vinculoId,
            @RequestHeader("X-Requester-Id") Long requesterId) {
        return exceptionHandler.withHandler(() -> {
            endVinculoMoradorUseCase.execute(vinculoId, requesterId);
            return ResponseEntity.noContent().build();
        });
    }
}
