package br.com.cesar.gestaoCondominial.aplicacao.dominium.unidade.usecase;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import br.com.cesar.gestaoCondominial.aplicacao.dominium.unidade.dto.UnidadeRequestDTO;
import br.com.cesar.gestaoCondominial.aplicacao.dominium.unidade.dto.UnidadeResponseDTO;
import br.com.cesar.gestaoCondominial.dominio.dominium.unidade.Unidade;
import br.com.cesar.gestaoCondominial.dominio.dominium.unidade.repository.UnidadeRepository;
import br.com.cesar.gestaoCondominial.dominio.dominium.usuario.Usuario;
import br.com.cesar.gestaoCondominial.dominio.dominium.usuario.repository.UsuarioRepository;

@Service
public class CreateUnidadeUseCase {

    private final UnidadeRepository unidadeRepository;
    private final UsuarioRepository usuarioRepository;

    public CreateUnidadeUseCase(UnidadeRepository unidadeRepository,
                               UsuarioRepository usuarioRepository) {
        this.unidadeRepository = unidadeRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public UnidadeResponseDTO execute(UnidadeRequestDTO request) {

        // 1. Validar unicidade (numero + bloco)
        if (unidadeRepository
                .findByNumeroAndBloco(request.getNumero(), request.getBloco())
                .isPresent()) {
            throw new IllegalArgumentException("Já existe uma unidade com esse número e bloco");
        }

        // 2. Buscar proprietário
        Usuario proprietario = usuarioRepository.findById(request.getProprietarioId())
                .orElseThrow(() -> new IllegalArgumentException("Proprietário não encontrado"));

        // 3. Buscar inquilino (se existir)
        Usuario inquilino = null;
        if (request.getInquilinoId() != null) {
            inquilino = usuarioRepository.findById(request.getInquilinoId())
                    .orElseThrow(() -> new IllegalArgumentException("Inquilino não encontrado"));
        }

        // 4. Criar entidade
        Unidade unidade = new Unidade();
        unidade.setNumero(request.getNumero());
        unidade.setBloco(request.getBloco());
        unidade.setProprietario(proprietario);
        unidade.setInquilino(inquilino);
        unidade.setStatus(request.getStatus());
        unidade.setSaldoDevedor(request.getSaldoDevedor());
        unidade.setCreatedAt(LocalDateTime.now());
        unidade.setUpdatedAt(LocalDateTime.now());

        // 5. Persistir
        Unidade savedUnidade = unidadeRepository.save(unidade);

        // 6. Retornar DTO
        return UnidadeResponseDTO.fromEntity(savedUnidade);
    }
}