package br.com.cesar.gestaoCondominial.operacional.aplicacao.funcionario.usecase;

import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.Funcionario;
import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.FuncionarioId;
import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.OrdemServico;
import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.OrdemServicoId;
import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.repository.FuncionarioRepository;
import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.repository.OrdemServicoRepository;
import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.DomainException;
import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.ResourceNotFoundException;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.TipoUsuario;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.Usuario;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.repository.UsuarioRepository;
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
