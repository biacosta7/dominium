package br.com.cesar.gestaoCondominial.moradores.aplicacao.unidade.usecase;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import br.com.cesar.gestaoCondominial.moradores.aplicacao.unidade.dto.UnidadeRequestDTO;
import br.com.cesar.gestaoCondominial.moradores.aplicacao.unidade.dto.UnidadeResponseDTO;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.Unidade;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.UnidadeId;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.repository.UnidadeRepository;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.Usuario;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.repository.UsuarioRepository;

@Service
public class UpdateUnidadeUseCase {

    private final UnidadeRepository unidadeRepository;
    private final UsuarioRepository usuarioRepository;

    public UpdateUnidadeUseCase(UnidadeRepository unidadeRepository,
                               UsuarioRepository usuarioRepository) {
        this.unidadeRepository = unidadeRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public UnidadeResponseDTO execute(Long id, UnidadeRequestDTO request) {

        // 1. Buscar unidade existente
        Unidade unidade = unidadeRepository.findById(new UnidadeId(id))
                .orElseThrow(() -> new IllegalArgumentException("Unidade não encontrada"));

        // 2. Validar unicidade (se numero/bloco mudaram)
        if (!unidade.getNumero().equals(request.getNumero()) ||
            !unidade.getBloco().equals(request.getBloco())) {

            unidadeRepository.findByNumeroAndBloco(
                    request.getNumero(), request.getBloco()
            ).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new IllegalArgumentException("Já existe uma unidade com esse número e bloco");
                }
            });
        }

        // 3. Buscar proprietário
        Usuario proprietario = usuarioRepository.findById(request.getProprietarioId())
                .orElseThrow(() -> new IllegalArgumentException("Proprietário não encontrado"));

        // 4. Buscar inquilino (opcional)
        Usuario inquilino = null;
        if (request.getInquilinoId() != null) {
            inquilino = usuarioRepository.findById(request.getInquilinoId())
                    .orElseThrow(() -> new IllegalArgumentException("Inquilino não encontrado"));
        }

        // 5. Atualizar campos
        unidade.setNumero(request.getNumero());
        unidade.setBloco(request.getBloco());
        unidade.setProprietario(proprietario);
        unidade.setInquilino(inquilino);
        unidade.setStatus(request.getStatus());
        unidade.setSaldoDevedor(request.getSaldoDevedor());
        unidade.setUpdatedAt(LocalDateTime.now());

        // 6. Salvar
        Unidade updated = unidadeRepository.save(unidade);

        // 7. Retornar DTO
        return UnidadeResponseDTO.fromEntity(updated);
    }
}