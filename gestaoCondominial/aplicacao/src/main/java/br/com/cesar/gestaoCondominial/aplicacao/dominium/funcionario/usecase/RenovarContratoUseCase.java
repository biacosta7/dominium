package br.com.cesar.gestaoCondominial.aplicacao.dominium.funcionario.usecase;

import br.com.cesar.gestaoCondominial.dominio.dominium.funcionario.Funcionario;
import br.com.cesar.gestaoCondominial.dominio.dominium.funcionario.FuncionarioId;
import br.com.cesar.gestaoCondominial.dominio.dominium.funcionario.repository.AvaliacaoFuncionarioRepository;
import br.com.cesar.gestaoCondominial.dominio.dominium.funcionario.repository.FuncionarioRepository;
import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.DomainException;
import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.ResourceNotFoundException;
import br.com.cesar.gestaoCondominial.dominio.dominium.usuario.TipoUsuario;
import br.com.cesar.gestaoCondominial.dominio.dominium.usuario.Usuario;
import br.com.cesar.gestaoCondominial.dominio.dominium.usuario.repository.UsuarioRepository;
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
