package br.com.cesar.gestaoCondominial.aplicacao.dominium.morador.usecase;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.cesar.gestaoCondominial.aplicacao.dominium.morador.dto.VinculoRequestDTO;
import br.com.cesar.gestaoCondominial.aplicacao.dominium.morador.dto.VinculoResponseDTO;
import br.com.cesar.gestaoCondominial.aplicacao.dominium.usuario.dto.UsuarioResponseDTO;
import br.com.cesar.gestaoCondominial.aplicacao.dominium.usuario.usecase.CreateUsuarioUseCase;
import br.com.cesar.gestaoCondominial.dominio.dominium.morador.StatusVinculo;
import br.com.cesar.gestaoCondominial.dominio.dominium.morador.VinculoMorador;
import br.com.cesar.gestaoCondominial.dominio.dominium.morador.repository.VinculoMoradorRepository;
import br.com.cesar.gestaoCondominial.dominio.dominium.unidade.Unidade;
import br.com.cesar.gestaoCondominial.dominio.dominium.unidade.UnidadeId;
import br.com.cesar.gestaoCondominial.dominio.dominium.unidade.repository.UnidadeRepository;
import br.com.cesar.gestaoCondominial.dominio.dominium.usuario.Usuario;
import br.com.cesar.gestaoCondominial.dominio.dominium.usuario.repository.UsuarioRepository;

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
    public VinculoResponseDTO execute(Long unidadeId, VinculoRequestDTO request) {
        Unidade unidade = unidadeRepository.findById(new UnidadeId(unidadeId))
                .orElseThrow(() -> new IllegalArgumentException("Unidade não encontrada"));

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
                .status(StatusVinculo.ATIVO)
                .build();

        VinculoMorador saved = vinculoMoradorRepository.save(novoVinculo);

        return VinculoResponseDTO.fromEntity(saved);
    }
}
