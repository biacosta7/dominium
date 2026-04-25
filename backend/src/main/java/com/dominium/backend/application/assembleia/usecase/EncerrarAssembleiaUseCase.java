package com.dominium.backend.application.assembleia.usecase;

import com.dominium.backend.domain.assembleia.Assembleia;
import com.dominium.backend.domain.assembleia.AssembleiaId;
import com.dominium.backend.domain.assembleia.repository.AssembleiaRepository;
import com.dominium.backend.domain.shared.exceptions.DomainException;
import com.dominium.backend.domain.shared.exceptions.ResourceNotFoundException;
import com.dominium.backend.domain.usuario.TipoUsuario;
import com.dominium.backend.domain.usuario.Usuario;
import com.dominium.backend.domain.usuario.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EncerrarAssembleiaUseCase {

    private final AssembleiaRepository assembleiaRepository;
    private final UsuarioRepository usuarioRepository;

    public EncerrarAssembleiaUseCase(AssembleiaRepository assembleiaRepository, UsuarioRepository usuarioRepository) {
        this.assembleiaRepository = assembleiaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Assembleia executar(Long sindicoId, String assembleiaId) {
        Usuario sindico = usuarioRepository.findById(sindicoId)
                .orElseThrow(() -> new DomainException("Usuário não encontrado"));

        if (sindico.getTipo() != TipoUsuario.SINDICO) {
            throw new DomainException("Apenas o síndico pode encerrar assembleias");
        }

        Assembleia assembleia = assembleiaRepository.findById(AssembleiaId.de(assembleiaId))
                .orElseThrow(() -> new ResourceNotFoundException("Assembleia não encontrada"));

        assembleia.encerrar();
        return assembleiaRepository.save(assembleia);
    }
}
