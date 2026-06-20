package br.com.cesar.gestaoCondominial.moradores.aplicacao.morador.usecase;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.cesar.gestaoCondominial.moradores.aplicacao.morador.dto.VinculoRequestDTO;
import br.com.cesar.gestaoCondominial.moradores.aplicacao.morador.dto.VinculoResponseDTO;
import br.com.cesar.gestaoCondominial.moradores.aplicacao.usuario.dto.UsuarioResponseDTO;
import br.com.cesar.gestaoCondominial.moradores.aplicacao.usuario.usecase.CreateUsuarioUseCase;
import br.com.cesar.gestaoCondominial.moradores.dominio.morador.StatusVinculo;
import br.com.cesar.gestaoCondominial.moradores.dominio.morador.VinculoMorador;
import br.com.cesar.gestaoCondominial.moradores.dominio.morador.repository.VinculoMoradorRepository;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.Unidade;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.UnidadeId;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.repository.UnidadeRepository;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.TipoUsuario;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.Usuario;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.repository.UsuarioRepository;
import br.com.cesar.gestaoCondominial.moradores.dominio.morador.TipoVinculo;
import java.util.List;

@Service
public class CreateVinculoMoradorUseCase {

    private final VinculoMoradorRepository vinculoMoradorRepository;
    private final UnidadeRepository unidadeRepository;
    private final UsuarioRepository usuarioRepository;
    private final CreateUsuarioUseCase createUsuarioUseCase;

    @Value("${dominium.unidade.max-moradores:5}")
    private int maxMoradores;

    public CreateVinculoMoradorUseCase(
            VinculoMoradorRepository vinculoMoradorRepository,
            UnidadeRepository unidadeRepository,
            UsuarioRepository usuarioRepository,
            CreateUsuarioUseCase createUsuarioUseCase) {
        this.vinculoMoradorRepository = vinculoMoradorRepository;
        this.unidadeRepository = unidadeRepository;
        this.usuarioRepository = usuarioRepository;
        this.createUsuarioUseCase = createUsuarioUseCase;
    }

    @Transactional
    public VinculoResponseDTO execute(Long unidadeId, VinculoRequestDTO request, Long requesterId) {
        Unidade unidade = unidadeRepository.findById(new UnidadeId(unidadeId))
                .orElseThrow(() -> new IllegalArgumentException("Unidade não encontrada"));

        StatusVinculo finalStatus = StatusVinculo.ATIVO;

        if (requesterId != null) {
            Usuario requester = usuarioRepository.findById(requesterId)
                    .orElseThrow(() -> new IllegalArgumentException("Usuário solicitante não encontrado"));

            if (requester.getTipo() != TipoUsuario.SINDICO) {
                List<VinculoMorador> vinculosSolicitante = vinculoMoradorRepository.findByUsuarioIdAndStatus(requesterId,
                        StatusVinculo.ATIVO);
                boolean isTitularDaMesmaUnidade = vinculosSolicitante.stream()
                        .anyMatch(v -> v.getTipo() == TipoVinculo.TITULAR &&
                                v.getUnidade().getId().equals(unidade.getId()));

                if (!isTitularDaMesmaUnidade) {
                    throw new IllegalStateException("Apenas o titular da unidade ou o síndico podem adicionar um morador");
                }
            }
        } else {
            // Self-registration (pending homologação)
            finalStatus = StatusVinculo.INATIVO;
        }

        long currentMoradores = vinculoMoradorRepository.countByUnidadeIdAndStatus(unidadeId, StatusVinculo.ATIVO);
        if (currentMoradores >= maxMoradores) {
            throw new IllegalStateException("Limite máximo de moradores por unidade atingido");
        }

        Usuario usuario;
        if (request.getNovoUsuario() != null) {
            UsuarioResponseDTO createdUserDto = createUsuarioUseCase.execute(request.getNovoUsuario());
            usuario = usuarioRepository.findById(createdUserDto.getId())
                    .orElseThrow(() -> new IllegalStateException("Falha ao recuperar usuário recém-criado"));
        } else if (request.getUsuarioId() != null) {
            usuario = usuarioRepository.findById(request.getUsuarioId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        } else {
            throw new IllegalArgumentException("É necessário informar o usuarioId ou os dados de um novoUsuario");
        }

        if (!vinculoMoradorRepository.findByUsuarioIdAndStatus(usuario.getId(), StatusVinculo.ATIVO).isEmpty()) {
            throw new IllegalStateException("Morador já possui vínculo ativo com outra unidade");
        }

        VinculoMorador novoVinculo = VinculoMorador.builder()
                .unidade(unidade)
                .usuario(usuario)
                .tipo(request.getTipo())
                .status(finalStatus)
                .build();

        VinculoMorador saved = vinculoMoradorRepository.save(novoVinculo);

        return VinculoResponseDTO.fromEntity(saved);
    }
}
