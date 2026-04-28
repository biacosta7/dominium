package br.com.cesar.gestaoCondominial.moradores.aplicacao.usuario.usecase;

import br.com.cesar.gestaoCondominial.moradores.aplicacao.security.PasswordEncryptor;
import br.com.cesar.gestaoCondominial.moradores.aplicacao.usuario.dto.UsuarioRequestDTO;
import br.com.cesar.gestaoCondominial.moradores.aplicacao.usuario.dto.UsuarioResponseDTO;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.Usuario;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.repository.UsuarioRepository;
import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.DomainException;
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
