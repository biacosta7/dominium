package com.dominium.backend.delivery.rest.unidade;

import com.dominium.backend.application.unidade.dto.UnidadeRequestDTO;
import com.dominium.backend.application.unidade.dto.UnidadeResponseDTO;
import com.dominium.backend.application.unidade.usecase.CreateUnidadeUseCase;
import com.dominium.backend.application.unidade.usecase.DeleteUnidadeUseCase;
// import com.dominium.backend.application.unidade.usecase.GetUnidadeUseCase;
// import com.dominium.backend.application.unidade.usecase.UpdateUnidadeUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/unidades")
public class UnidadeController {
    private final CreateUnidadeUseCase createUnidadeUseCase;
//     private final GetUnidadeUseCase getUnidadeUseCase;
//     private final UpdateUnidadeUseCase updateUnidadeUseCase;
    private final DeleteUnidadeUseCase deleteUnidadeUseCase;

    public UnidadeController(CreateUnidadeUseCase createUnidadeUseCase, DeleteUnidadeUseCase deleteUnidadeUseCase){
        this.createUnidadeUseCase = createUnidadeUseCase;
        this.deleteUnidadeUseCase = deleteUnidadeUseCase;
    }
    @PostMapping
    public ResponseEntity<UnidadeResponseDTO> create(@Valid @RequestBody UnidadeRequestDTO request){
        UnidadeResponseDTO response = createUnidadeUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteUnidadeUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }   
}

//     @GetMapping
//     public ResponseEntity<List<UsuarioResponseDTO>> findAll() {
//         return ResponseEntity.ok(getUsuarioUseCase.findAll());
//     }

//     @GetMapping("/{id}")
//     public ResponseEntity<UsuarioResponseDTO> findById(@PathVariable Long id) {
//         return ResponseEntity.ok(getUsuarioUseCase.findById(id));
//     }

//     @PutMapping("/{id}")
//     public ResponseEntity<UsuarioResponseDTO> update(@PathVariable Long id, @Valid @RequestBody UsuarioRequestDTO request) {
//         return ResponseEntity.ok(updateUsuarioUseCase.execute(id, request));
//     }

//     @DeleteMapping("/{id}")
//     public ResponseEntity<Void> delete(@PathVariable Long id) {
//         deleteUsuarioUseCase.execute(id);
//         return ResponseEntity.noContent().build();
//     }
// }
