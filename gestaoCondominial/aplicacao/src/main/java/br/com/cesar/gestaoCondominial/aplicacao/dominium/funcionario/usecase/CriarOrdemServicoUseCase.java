package com.dominium.backend.application.funcionario.usecase;

import com.dominium.backend.domain.funcionario.repository.FuncionarioRepository;
import com.dominium.backend.domain.funcionario.repository.OrdemServicoRepository;
import com.dominium.backend.domain.shared.exceptions.DomainException;
import com.dominium.backend.domain.shared.exceptions.ResourceNotFoundException;
import com.dominium.backend.domain.usuario.TipoUsuario;
import com.dominium.backend.domain.usuario.Usuario;
import com.dominium.backend.domain.usuario.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class CriarOrdemServicoUseCase {

    private final OrdemServicoRepository ordemServicoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final UsuarioRepository usuarioRepository;

    public CriarOrdemServicoUseCase(OrdemServicoRepository ordemServicoRepository,
                                     FuncionarioRepository funcionarioRepository,
                                     UsuarioRepository usuarioRepository) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public OrdemServico executar(Long sindicoId, String funcionarioId, String descricao,
                                  LocalDate dataInicio, LocalDate dataFim) {
        Usuario sindico = usuarioRepository.findById(sindicoId)
                .orElseThrow(() -> new DomainException("Usuário não encontrado"));

        if (sindico.getTipo() != TipoUsuario.SINDICO) {
            throw new DomainException("Apenas o síndico pode criar ordens de serviço");
        }

        Funcionario funcionario = funcionarioRepository.findById(FuncionarioId.de(funcionarioId))
                .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado"));

        if (funcionario.getTipoVinculo() != TipoVinculo.EVENTUAL) {
            throw new DomainException("Ordens de serviço são exclusivas para prestadores eventuais");
        }

        OrdemServico os = OrdemServico.criar(
                OrdemServicoId.novo(), descricao,
                funcionario.getId(), dataInicio, dataFim
        );
        OrdemServico salva = ordemServicoRepository.save(os);

        // ativa o prestador eventual ao vincular a uma OS
        funcionario.ativar();
        funcionarioRepository.save(funcionario);

        return salva;
    }
}
