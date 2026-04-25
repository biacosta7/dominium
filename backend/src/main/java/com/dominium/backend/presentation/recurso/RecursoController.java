package com.dominium.backend.presentation.recurso;

import com.dominium.backend.application.recurso.dto.AbrirRecursoRequestDTO;
import com.dominium.backend.application.recurso.dto.JulgarRecursoRequestDTO;
import com.dominium.backend.application.recurso.usecase.AbrirRecursoUseCase;
import com.dominium.backend.application.recurso.usecase.JulgarRecursoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/recursos")
@RequiredArgsConstructor
public class RecursoController {

    private final AbrirRecursoUseCase abrirRecursoUseCase;
    private final JulgarRecursoUseCase julgarRecursoUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UUID abrirRecurso(@RequestBody AbrirRecursoRequestDTO request) {
        return abrirRecursoUseCase.execute(request);
    }

    @PutMapping("/{id}/julgar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void julgarRecurso(@PathVariable UUID id, @RequestBody JulgarRecursoRequestDTO request) {
        julgarRecursoUseCase.execute(id, request);
    }
}