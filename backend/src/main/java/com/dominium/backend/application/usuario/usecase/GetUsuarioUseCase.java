package com.dominium.backend.application.usuario.usecase;

import com.dominium.backend.application.usuario.dto.UsuarioResponseDTO;
import com.dominium.backend.domain.usuario.Usuario;
import com.dominium.backend.domain.usuario.repository.UsuarioRepository;
import com.dominium.backend.domain.shared.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;

    public GetUsuarioUseCase(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<UsuarioResponseDTO> findAll() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public UsuarioResponseDTO findById(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        return UsuarioResponseDTO.fromEntity(usuario);
    }
}
