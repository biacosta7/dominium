package br.com.cesar.gestaoCondominial.aplicacao.dominium.usuario.usecase;

import br.com.cesar.gestaoCondominial.dominio.dominium.usuario.repository.UsuarioRepository;
import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.ResourceNotFoundException;
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
