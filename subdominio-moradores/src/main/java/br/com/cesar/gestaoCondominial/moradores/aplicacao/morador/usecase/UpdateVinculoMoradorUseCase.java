package br.com.cesar.gestaoCondominial.moradores.aplicacao.morador.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.cesar.gestaoCondominial.moradores.aplicacao.morador.dto.VinculoRequestDTO;
import br.com.cesar.gestaoCondominial.moradores.aplicacao.morador.dto.VinculoResponseDTO;
import br.com.cesar.gestaoCondominial.moradores.dominio.morador.VinculoMorador;
import br.com.cesar.gestaoCondominial.moradores.dominio.morador.repository.VinculoMoradorRepository;

import br.com.cesar.gestaoCondominial.moradores.dominio.morador.StatusVinculo;
import br.com.cesar.gestaoCondominial.moradores.dominio.morador.TipoVinculo;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.TipoUsuario;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.Usuario;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.repository.UsuarioRepository;
import java.util.List;

@Service
public class UpdateVinculoMoradorUseCase {

    private final VinculoMoradorRepository vinculoMoradorRepository;
    private final UsuarioRepository usuarioRepository;

    public UpdateVinculoMoradorUseCase(VinculoMoradorRepository vinculoMoradorRepository,
            UsuarioRepository usuarioRepository) {
        this.vinculoMoradorRepository = vinculoMoradorRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public VinculoResponseDTO execute(Long vinculoId, VinculoRequestDTO request, Long requesterId) {
        if (requesterId == null) {
            throw new IllegalArgumentException("Id do solicitante é obrigatório");
        }

        VinculoMorador vinculo = vinculoMoradorRepository.findById(vinculoId)
                .orElseThrow(() -> new IllegalArgumentException("Vínculo não encontrado"));

        Usuario requester = usuarioRepository.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário solicitante não encontrado"));

        if (requester.getTipo() != TipoUsuario.SINDICO) {
            List<VinculoMorador> vinculosSolicitante = vinculoMoradorRepository.findByUsuarioIdAndStatus(requesterId,
                    StatusVinculo.ATIVO);
            boolean isTitularDaMesmaUnidade = vinculosSolicitante.stream()
                    .anyMatch(v -> v.getTipo() == TipoVinculo.TITULAR &&
                            v.getUnidade().equals(vinculo.getUnidade()));

            if (!isTitularDaMesmaUnidade) {
                throw new IllegalStateException("Apenas o titular da unidade ou o síndico podem atualizar um morador");
            }
        }

        if (request.getTipo() != null) {
            vinculo.setTipo(request.getTipo());
        }

        VinculoMorador updated = vinculoMoradorRepository.save(vinculo);

        return VinculoResponseDTO.fromEntity(updated);
    }
}
