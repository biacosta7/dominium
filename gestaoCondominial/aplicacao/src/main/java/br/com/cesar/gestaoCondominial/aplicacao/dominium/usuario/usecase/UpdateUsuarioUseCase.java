package br.com.cesar.gestaoCondominial.aplicacao.dominium.usuario.usecase;

import br.com.cesar.gestaoCondominial.aplicacao.dominium.security.PasswordEncryptor;
import br.com.cesar.gestaoCondominial.aplicacao.dominium.usuario.dto.UsuarioRequestDTO;
import br.com.cesar.gestaoCondominial.aplicacao.dominium.usuario.dto.UsuarioResponseDTO;
import br.com.cesar.gestaoCondominial.dominio.dominium.usuario.Usuario;
import br.com.cesar.gestaoCondominial.dominio.dominium.usuario.repository.UsuarioRepository;
import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.DomainException;
import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.ResourceNotFoundException;
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
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (!usuario.getEmail().equals(request.getEmail()) && 
            usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DomainException("Email já cadastrado para outro usuário");
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
