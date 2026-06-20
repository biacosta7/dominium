package br.com.cesar.gestaoCondominial.apresentacao.dominium.usuario;

import br.com.cesar.gestaoCondominial.moradores.aplicacao.usuario.dto.UsuarioRequestDTO;
import br.com.cesar.gestaoCondominial.moradores.aplicacao.usuario.dto.UsuarioResponseDTO;
import br.com.cesar.gestaoCondominial.moradores.aplicacao.usuario.usecase.CreateUsuarioUseCase;
import br.com.cesar.gestaoCondominial.moradores.aplicacao.usuario.usecase.DeleteUsuarioUseCase;
import br.com.cesar.gestaoCondominial.moradores.aplicacao.usuario.usecase.GetUsuarioUseCase;
import br.com.cesar.gestaoCondominial.moradores.aplicacao.usuario.usecase.UpdateUsuarioUseCase;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.repository.UsuarioRepository;
import br.com.cesar.gestaoCondominial.moradores.aplicacao.security.PasswordEncryptor;
import br.com.cesar.gestaoCondominial.apresentacao.dominium.exception.ExceptionHandler;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final CreateUsuarioUseCase createUsuarioUseCase;
    private final GetUsuarioUseCase getUsuarioUseCase;
    private final UpdateUsuarioUseCase updateUsuarioUseCase;
    private final DeleteUsuarioUseCase deleteUsuarioUseCase;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncryptor passwordEncryptor;
    private final ExceptionHandler exceptionHandler;

    public UsuarioController(
            CreateUsuarioUseCase createUsuarioUseCase,
            GetUsuarioUseCase getUsuarioUseCase,
            UpdateUsuarioUseCase updateUsuarioUseCase,
            DeleteUsuarioUseCase deleteUsuarioUseCase,
            UsuarioRepository usuarioRepository,
            PasswordEncryptor passwordEncryptor,
            ExceptionHandler exceptionHandler) {
        this.createUsuarioUseCase = createUsuarioUseCase;
        this.getUsuarioUseCase = getUsuarioUseCase;
        this.updateUsuarioUseCase = updateUsuarioUseCase;
        this.deleteUsuarioUseCase = deleteUsuarioUseCase;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncryptor = passwordEncryptor;
        this.exceptionHandler = exceptionHandler;
    }

    public static class LoginRequest {
        private String email;
        private String senha;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getSenha() { return senha; }
        public void setSenha(String senha) { this.senha = senha; }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return exceptionHandler.withHandler(() -> {
            var usuario = usuarioRepository.findByEmail(request.getEmail().trim().toLowerCase())
                    .orElseThrow(() -> new IllegalArgumentException("Credenciais inválidas"));

            boolean matches = passwordEncryptor.matches(request.getSenha(), usuario.getSenha())
                    || request.getSenha().equals(usuario.getSenha());

            if (!matches) {
                throw new IllegalArgumentException("Credenciais inválidas");
            }

            return ResponseEntity.ok(UsuarioResponseDTO.fromEntity(usuario));
        });
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody UsuarioRequestDTO request) {
        return exceptionHandler.withHandler(() -> {
            UsuarioResponseDTO response = createUsuarioUseCase.execute(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        });
    }

    @GetMapping
    public ResponseEntity<?> findAll() {
        return exceptionHandler.withHandler(() -> ResponseEntity.ok(getUsuarioUseCase.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return exceptionHandler.withHandler(() -> ResponseEntity.ok(getUsuarioUseCase.findById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody UsuarioRequestDTO request) {
        return exceptionHandler.withHandler(() -> ResponseEntity.ok(updateUsuarioUseCase.execute(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return exceptionHandler.withHandler(() -> {
            deleteUsuarioUseCase.execute(id);
            return ResponseEntity.noContent().build();
        });
    }
}
