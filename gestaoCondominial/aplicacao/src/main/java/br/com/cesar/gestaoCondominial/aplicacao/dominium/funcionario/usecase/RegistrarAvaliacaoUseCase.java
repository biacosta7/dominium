package com.dominium.backend.application.funcionario.usecase;

import com.dominium.backend.domain.funcionario.AvaliacaoFuncionario;
import com.dominium.backend.domain.funcionario.FuncionarioId;
import com.dominium.backend.domain.funcionario.repository.AvaliacaoFuncionarioRepository;
import com.dominium.backend.domain.funcionario.repository.FuncionarioRepository;
import com.dominium.backend.domain.shared.exceptions.DomainException;
import com.dominium.backend.domain.shared.exceptions.ResourceNotFoundException;
import com.dominium.backend.domain.usuario.TipoUsuario;
import com.dominium.backend.domain.usuario.Usuario;
import com.dominium.backend.domain.usuario.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrarAvaliacaoUseCase {

    private final AvaliacaoFuncionarioRepository avaliacaoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final UsuarioRepository usuarioRepository;

    public RegistrarAvaliacaoUseCase(AvaliacaoFuncionarioRepository avaliacaoRepository,
                                      FuncionarioRepository funcionarioRepository,
                                      UsuarioRepository usuarioRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public AvaliacaoFuncionario executar(Long sindicoId, String funcionarioId, boolean positiva, String comentario) {
        Usuario sindico = usuarioRepository.findById(sindicoId)
                .orElseThrow(() -> new DomainException("Usuário não encontrado"));

        if (sindico.getTipo() != TipoUsuario.SINDICO) {
            throw new DomainException("Apenas o síndico pode registrar avaliações");
        }

        FuncionarioId fid = FuncionarioId.de(funcionarioId);
        funcionarioRepository.findById(fid)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado"));

        AvaliacaoFuncionario avaliacao = AvaliacaoFuncionario.criar(fid, positiva, comentario);
        return avaliacaoRepository.save(avaliacao);
    }
}
