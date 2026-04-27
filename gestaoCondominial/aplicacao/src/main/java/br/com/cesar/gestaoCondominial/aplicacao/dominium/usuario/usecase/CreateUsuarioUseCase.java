package com.dominium.backend.application.usuario.usecase;

import com.dominium.backend.application.security.PasswordEncryptor;
import com.dominium.backend.application.usuario.dto.UsuarioRequestDTO;
import com.dominium.backend.application.usuario.dto.UsuarioResponseDTO;
import com.dominium.backend.domain.usuario.Usuario;
import com.dominium.backend.domain.usuario.repository.UsuarioRepository;
import com.dominium.backend.domain.shared.exceptions.DomainException;
import org.springframework.stereotype.Service;

@Service
public class CreateUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncryptor passwordEncryptor;

    public CreateUsuarioUseCase(UsuarioRepository usuarioRepository, PasswordEncryptor passwordEncryptor) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncryptor = passwordEncryptor;
    }

    public UsuarioResponseDTO execute(UsuarioRequestDTO request) {
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DomainException("Email já cadastrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setSenha(passwordEncryptor.encode(request.getSenha()));
        usuario.setTelefone(request.getTelefone());
        usuario.setCpf(request.getCpf());
        usuario.setTipo(request.getTipo());

        Usuario savedUser = usuarioRepository.save(usuario);
        
        return UsuarioResponseDTO.fromEntity(savedUser);
    }
}
