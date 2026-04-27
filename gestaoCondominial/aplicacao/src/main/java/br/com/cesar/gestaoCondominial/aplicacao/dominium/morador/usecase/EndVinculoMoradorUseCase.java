package br.com.cesar.gestaoCondominial.aplicacao.dominium.morador.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.cesar.gestaoCondominial.dominio.dominium.morador.StatusVinculo;
import br.com.cesar.gestaoCondominial.dominio.dominium.morador.TipoVinculo;
import br.com.cesar.gestaoCondominial.dominio.dominium.morador.VinculoMorador;
import br.com.cesar.gestaoCondominial.dominio.dominium.morador.repository.VinculoMoradorRepository;
import br.com.cesar.gestaoCondominial.dominio.dominium.usuario.TipoUsuario;
import br.com.cesar.gestaoCondominial.dominio.dominium.usuario.Usuario;
import br.com.cesar.gestaoCondominial.dominio.dominium.usuario.repository.UsuarioRepository;

import java.util.List;

@Service
public class EndVinculoMoradorUseCase {

    private final VinculoMoradorRepository vinculoMoradorRepository;
    private final UsuarioRepository usuarioRepository;

    public EndVinculoMoradorUseCase(VinculoMoradorRepository vinculoMoradorRepository,
            UsuarioRepository usuarioRepository) {
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

            List<VinculoMorador> vinculosSolicitante = vinculoMoradorRepository.findByUsuarioIdAndStatus(requesterId,
                    StatusVinculo.ATIVO);
            boolean isTitularDaMesmaUnidade = vinculosSolicitante.stream()
                    .anyMatch(v -> v.getTipo() == TipoVinculo.TITULAR &&
                            v.getUnidade().equals(vinculoParaRemover.getUnidade()));

            if (!isTitularDaMesmaUnidade) {
                throw new IllegalStateException("Apenas o titular da unidade ou o síndico podem remover um morador");
            }
        }

        vinculoParaRemover.setStatus(StatusVinculo.INATIVO);
        vinculoMoradorRepository.save(vinculoParaRemover);
    }
}
