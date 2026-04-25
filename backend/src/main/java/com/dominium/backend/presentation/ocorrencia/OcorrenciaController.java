package com.dominium.backend.presentation.ocorrencia;

import com.dominium.backend.application.ocorrencia.dto.OcorrenciaRequestDTO;
import com.dominium.backend.application.ocorrencia.usecase.GerenciarOcorrenciaUseCase;
import com.dominium.backend.domain.ocorrencia.Ocorrencia;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ocorrencias")
public class OcorrenciaController {

    private final GerenciarOcorrenciaUseCase useCase;


    public OcorrenciaController(GerenciarOcorrenciaUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    public ResponseEntity<Ocorrencia> criar(@RequestBody OcorrenciaRequestDTO dto) {
        Ocorrencia novaOcorrencia = useCase.executar(dto);
        return ResponseEntity.ok(novaOcorrencia);
    }
}