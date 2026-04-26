package com.dominium.backend.application.morador.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dominium.backend.domain.morador.StatusVinculo;
import com.dominium.backend.domain.morador.TipoVinculo;
import com.dominium.backend.domain.morador.VinculoMorador;
import com.dominium.backend.domain.morador.repository.VinculoMoradorRepository;
import com.dominium.backend.domain.usuario.TipoUsuario;
import com.dominium.backend.domain.usuario.Usuario;
import com.dominium.backend.domain.usuario.repository.UsuarioRepository;

import java.util.List;

@Service
public class EndVinculoMoradorUseCase {

    private final VinculoMoradorRepository vinculoMoradorRepository;
    private final UsuarioRepository usuarioRepository;

    public EndVinculoMoradorUseCase(VinculoMoradorRepository vinculoMoradorRepository, UsuarioRepository usuarioRepository) {
        this.vinculoMoradorRepository = vinculoMoradorRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public void execute(Long vinculoId, Long requesterId) {
        VinculoMorador vinculoParaRemover = vinculoMoradorRepository.findById(vinculoId)
                .orElseThrow(() -> new IllegalArgumentException("Vínculo não encontrado"));

        Usuario requester = usuarioRepository.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário solicitante não encontrado"));

        // Se for síndico, tem permissão total
        if (requester.getTipo() != TipoUsuario.SINDICO) {
            // Se não for síndico, verifica se o solicitante é titular da mesma unidade
            Long unidadeId = vinculoParaRemover.getUnidade().getId().getValor();
            
            List<VinculoMorador> vinculosSolicitante = vinculoMoradorRepository.findByUsuarioIdAndStatus(requesterId, StatusVinculo.ATIVO);
            boolean isTitularDaMesmaUnidade = vinculosSolicitante.stream()
                    .anyMatch(v -> v.getUnidade().getId().equals(unidadeId) && v.getTipo() == TipoVinculo.TITULAR);
            
            if (!isTitularDaMesmaUnidade) {
                throw new IllegalStateException("Apenas o titular da unidade ou o síndico podem remover um morador");
            }
        }

        vinculoParaRemover.setStatus(StatusVinculo.INATIVO);
        vinculoMoradorRepository.save(vinculoParaRemover);
    }
}
