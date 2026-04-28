package br.com.cesar.gestaoCondominial.apresentacao.dominium.unidade;

import br.com.cesar.gestaoCondominial.aplicacao.dominium.unidade.dto.HistoricoTitularidadeResponseDTO;
import br.com.cesar.gestaoCondominial.aplicacao.dominium.unidade.dto.UnidadeRequestDTO;
import br.com.cesar.gestaoCondominial.aplicacao.dominium.unidade.dto.UnidadeResponseDTO;
import br.com.cesar.gestaoCondominial.aplicacao.dominium.unidade.usecase.CreateUnidadeUseCase;
import br.com.cesar.gestaoCondominial.aplicacao.dominium.unidade.usecase.DeleteUnidadeUseCase;
import br.com.cesar.gestaoCondominial.aplicacao.dominium.unidade.usecase.GetHistoricoTitularidadeUseCase;
import br.com.cesar.gestaoCondominial.aplicacao.dominium.unidade.usecase.GetUnidadeUseCase;
import br.com.cesar.gestaoCondominial.aplicacao.dominium.unidade.usecase.TransferirTitularidadeUseCase;
import br.com.cesar.gestaoCondominial.aplicacao.dominium.unidade.usecase.UpdateUnidadeUseCase;
// import br.com.cesar.gestaoCondominial.aplicacao.dominium.unidade.usecase.GetUnidadeUseCase;
// import br.com.cesar.gestaoCondominial.aplicacao.dominium.unidade.usecase.UpdateUnidadeUseCase;
import br.com.cesar.gestaoCondominial.apresentacao.dominium.exception.ExceptionHandler;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/unidades")
public class UnidadeController {
    private final CreateUnidadeUseCase createUnidadeUseCase;
    private final GetUnidadeUseCase getUnidadeUseCase;
    private final UpdateUnidadeUseCase updateUnidadeUseCase;
    private final DeleteUnidadeUseCase deleteUnidadeUseCase;
    private final TransferirTitularidadeUseCase transferirTitularidadeUseCase;
    private final GetHistoricoTitularidadeUseCase getHistoricoTitularidadeUseCase;
    private final ExceptionHandler exceptionHandler;

    public UnidadeController(CreateUnidadeUseCase createUnidadeUseCase, DeleteUnidadeUseCase deleteUnidadeUseCase, GetUnidadeUseCase getUnidadeUseCase, UpdateUnidadeUseCase updateUnidadeUseCase, TransferirTitularidadeUseCase transferirTitularidadeUseCase, GetHistoricoTitularidadeUseCase getHistoricoTitularidadeUseCase, ExceptionHandler exceptionHandler){
        this.createUnidadeUseCase = createUnidadeUseCase;
        this.deleteUnidadeUseCase = deleteUnidadeUseCase;
        this.getUnidadeUseCase = getUnidadeUseCase;
        this.updateUnidadeUseCase = updateUnidadeUseCase;
        this.transferirTitularidadeUseCase = transferirTitularidadeUseCase;
        this.getHistoricoTitularidadeUseCase = getHistoricoTitularidadeUseCase;
        this.exceptionHandler = exceptionHandler;
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody UnidadeRequestDTO request){
        return exceptionHandler.withHandler(() -> {
            UnidadeResponseDTO response = createUnidadeUseCase.execute(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        });
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return exceptionHandler.withHandler(() -> {
            deleteUnidadeUseCase.execute(id);
            return ResponseEntity.noContent().build();
        });
    }   

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id){
        return exceptionHandler.withHandler(() -> ResponseEntity.ok(getUnidadeUseCase.findById(id)));
    }

    @GetMapping
    public ResponseEntity<?> findAll(){
        return exceptionHandler.withHandler(() -> ResponseEntity.ok(getUnidadeUseCase.findAll()));
    }
    @PutMapping("/{id}")
    public ResponseEntity<UnidadeResponseDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid UnidadeRequestDTO request) {

        UnidadeResponseDTO response = updateUnidadeUseCase.execute(id, request);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/{id}/transferir-titularidade")
    public ResponseEntity<Void> transferir(
            @PathVariable Long id,
            @RequestParam Long novoProprietarioId) {

        transferirTitularidadeUseCase.execute(id, novoProprietarioId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}/historico")
    public ResponseEntity<List<HistoricoTitularidadeResponseDTO>> getHistorico(@PathVariable Long id) {
        
        List<HistoricoTitularidadeResponseDTO> response =
                getHistoricoTitularidadeUseCase.execute(id);

        return ResponseEntity.ok(response);
    }
}

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
