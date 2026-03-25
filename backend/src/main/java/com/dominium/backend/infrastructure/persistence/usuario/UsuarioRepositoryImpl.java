package com.dominium.backend.infrastructure.persistence.usuario;

import com.dominium.backend.domain.usuario.Usuario;
import com.dominium.backend.domain.usuario.repository.UsuarioRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UsuarioRepositoryImpl implements UsuarioRepository {

    private final SpringDataUsuarioRepository springDataRepository;

    public UsuarioRepositoryImpl(SpringDataUsuarioRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Usuario save(Usuario usuario) {
        return springDataRepository.save(usuario);
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return springDataRepository.findById(id);
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return springDataRepository.findByEmail(email);
    }

    @Override
    public List<Usuario> findAll() {
        return springDataRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        springDataRepository.deleteById(id);
    }
}
