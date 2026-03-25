package com.dominium.backend.application.usuario.usecase;

import com.dominium.backend.application.security.PasswordEncryptor;
import com.dominium.backend.application.usuario.dto.UsuarioRequestDTO;
import com.dominium.backend.application.usuario.dto.UsuarioResponseDTO;
import com.dominium.backend.domain.usuario.Usuario;
import com.dominium.backend.domain.usuario.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class UpdateUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncryptor passwordEncryptor;

    public UpdateUsuarioUseCase(UsuarioRepository usuarioRepository, PasswordEncryptor passwordEncryptor) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncryptor = passwordEncryptor;
    }

    public UsuarioResponseDTO execute(Long id, UsuarioRequestDTO request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        if (!usuario.getEmail().equals(request.getEmail()) && 
            usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado para outro usuário");
        }

        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setTelefone(request.getTelefone());
        usuario.setCpf(request.getCpf());
        usuario.setTipo(request.getTipo());

        if (request.getSenha() != null && !request.getSenha().isBlank()) {
            usuario.setSenha(passwordEncryptor.encode(request.getSenha()));
        }

        Usuario updatedUser = usuarioRepository.save(usuario);
        
        return UsuarioResponseDTO.fromEntity(updatedUser);
    }
}
