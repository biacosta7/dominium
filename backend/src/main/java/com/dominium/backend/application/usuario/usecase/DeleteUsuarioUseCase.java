package com.dominium.backend.application.usuario.usecase;

import com.dominium.backend.domain.usuario.repository.UsuarioRepository;
import com.dominium.backend.domain.shared.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DeleteUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;

    public DeleteUsuarioUseCase(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public void execute(Long id) {
        usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        usuarioRepository.deleteById(id);
    }
}
