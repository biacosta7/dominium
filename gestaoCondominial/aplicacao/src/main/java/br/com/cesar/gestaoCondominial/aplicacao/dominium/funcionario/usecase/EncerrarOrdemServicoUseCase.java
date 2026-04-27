package com.dominium.backend.application.funcionario.usecase;

import com.dominium.backend.domain.funcionario.Funcionario;
import com.dominium.backend.domain.funcionario.FuncionarioId;
import com.dominium.backend.domain.funcionario.OrdemServico;
import com.dominium.backend.domain.funcionario.OrdemServicoId;
import com.dominium.backend.domain.funcionario.repository.FuncionarioRepository;
import com.dominium.backend.domain.funcionario.repository.OrdemServicoRepository;
import com.dominium.backend.domain.shared.exceptions.DomainException;
import com.dominium.backend.domain.shared.exceptions.ResourceNotFoundException;
import com.dominium.backend.domain.usuario.TipoUsuario;
import com.dominium.backend.domain.usuario.Usuario;
import com.dominium.backend.domain.usuario.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EncerrarOrdemServicoUseCase {

    private final OrdemServicoRepository ordemServicoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final UsuarioRepository usuarioRepository;

    public EncerrarOrdemServicoUseCase(OrdemServicoRepository ordemServicoRepository,
                                        FuncionarioRepository funcionarioRepository,
                                        UsuarioRepository usuarioRepository) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public OrdemServico executar(Long sindicoId, String ordemServicoId) {
        Usuario sindico = usuarioRepository.findById(sindicoId)
                .orElseThrow(() -> new DomainException("Usuário não encontrado"));

        if (sindico.getTipo() != TipoUsuario.SINDICO) {
            throw new DomainException("Apenas o síndico pode encerrar ordens de serviço");
        }

        OrdemServico os = ordemServicoRepository.findById(OrdemServicoId.de(ordemServicoId))
                .orElseThrow(() -> new ResourceNotFoundException("Ordem de serviço não encontrada"));

        os.concluir();
        ordemServicoRepository.save(os);

        // inativa o prestador eventual quando não tem mais OS ativa
        boolean possuiOutraOsAtiva = ordemServicoRepository.existeAtivaParaFuncionario(os.getFuncionarioId());
        if (!possuiOutraOsAtiva) {
            Funcionario funcionario = funcionarioRepository.findById(os.getFuncionarioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado"));
            funcionario.inativar();
            funcionarioRepository.save(funcionario);
        }

        return os;
    }
}
