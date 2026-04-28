package br.com.cesar.gestaoCondominial.moradores.aplicacao.usuario.usecase;

import br.com.cesar.gestaoCondominial.moradores.aplicacao.usuario.dto.UsuarioResponseDTO;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.Usuario;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.repository.UsuarioRepository;
import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.ResourceNotFoundException;
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
