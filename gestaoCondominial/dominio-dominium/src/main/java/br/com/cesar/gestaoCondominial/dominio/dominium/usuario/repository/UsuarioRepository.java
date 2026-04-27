package br.com.cesar.gestaoCondominial.dominio.dominium.usuario.repository;

import br.com.cesar.gestaoCondominial.dominio.dominium.usuario.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository {
    Usuario save(Usuario usuario);
    Optional<Usuario> findById(Long id);
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findAll();
    void deleteById(Long id);
}
