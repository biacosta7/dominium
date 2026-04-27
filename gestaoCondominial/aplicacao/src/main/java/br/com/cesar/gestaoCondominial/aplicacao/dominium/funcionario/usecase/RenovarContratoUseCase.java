package com.dominium.backend.application.funcionario.usecase;

import com.dominium.backend.domain.funcionario.Funcionario;
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

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class RenovarContratoUseCase {

    private static final int LIMITE_AVALIACOES_NEGATIVAS = 3;

    private final FuncionarioRepository funcionarioRepository;
    private final AvaliacaoFuncionarioRepository avaliacaoRepository;
    private final UsuarioRepository usuarioRepository;

    public RenovarContratoUseCase(FuncionarioRepository funcionarioRepository,
                                   AvaliacaoFuncionarioRepository avaliacaoRepository,
                                   UsuarioRepository usuarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
        this.avaliacaoRepository = avaliacaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Funcionario executar(Long sindicoId, String funcionarioId, LocalDate novaDataFim, BigDecimal novoValor) {
        Usuario sindico = usuarioRepository.findById(sindicoId)
                .orElseThrow(() -> new DomainException("Usuário não encontrado"));

        if (sindico.getTipo() != TipoUsuario.SINDICO) {
            throw new DomainException("Apenas o síndico pode renovar contratos");
        }

        Funcionario funcionario = funcionarioRepository.findById(FuncionarioId.de(funcionarioId))
                .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado"));

        long negativasRecentes = avaliacaoRepository.contarNegativasRecentes(
                funcionario.getId(), LIMITE_AVALIACOES_NEGATIVAS
        );

        funcionario.renovarContrato(novaDataFim, novoValor, negativasRecentes);
        return funcionarioRepository.save(funcionario);
    }
}
