package com.dominium.backend.presentation.assembleia;

import com.dominium.backend.application.assembleia.dto.AssembleiaRequestDTO;
import com.dominium.backend.application.assembleia.dto.AssembleiaResponseDTO;
import com.dominium.backend.application.assembleia.dto.VotoRequestDTO;
import com.dominium.backend.application.assembleia.usecase.CriarAssembleiaUseCase;
import com.dominium.backend.application.assembleia.usecase.VotarPautaUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assembleias")
public class AssembleiaController {

    private final CriarAssembleiaUseCase criarAssembleiaUseCase;
    private final VotarPautaUseCase votarPautaUseCase;

    public AssembleiaController(CriarAssembleiaUseCase criarAssembleiaUseCase, VotarPautaUseCase votarPautaUseCase) {
        this.criarAssembleiaUseCase = criarAssembleiaUseCase;
        this.votarPautaUseCase = votarPautaUseCase;
    }

    @PostMapping
    public ResponseEntity<AssembleiaResponseDTO> agendarAssembleia(@RequestBody AssembleiaRequestDTO request) {
        AssembleiaResponseDTO response = criarAssembleiaUseCase.executar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{assembleiaId}/pautas/{pautaId}/votar")
    public ResponseEntity<Void> votar(
            @PathVariable String assembleiaId,
            @PathVariable String pautaId,
            @RequestBody VotoRequestDTO request) {

        votarPautaUseCase.executar(assembleiaId, pautaId, request);
        return ResponseEntity.ok().build();
    }
}