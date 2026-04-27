package br.com.cesar.gestaoCondominial.aplicacao.dominium.funcionario.usecase;

import br.com.cesar.gestaoCondominial.dominio.dominium.funcionario.Funcionario;
import br.com.cesar.gestaoCondominial.dominio.dominium.funcionario.FuncionarioId;
import br.com.cesar.gestaoCondominial.dominio.dominium.funcionario.OrdemServico;
import br.com.cesar.gestaoCondominial.dominio.dominium.funcionario.OrdemServicoId;
import br.com.cesar.gestaoCondominial.dominio.dominium.funcionario.TipoVinculo;
import br.com.cesar.gestaoCondominial.dominio.dominium.funcionario.repository.FuncionarioRepository;
import br.com.cesar.gestaoCondominial.dominio.dominium.funcionario.repository.OrdemServicoRepository;
import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.DomainException;
import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.ResourceNotFoundException;
import br.com.cesar.gestaoCondominial.dominio.dominium.usuario.TipoUsuario;
import br.com.cesar.gestaoCondominial.dominio.dominium.usuario.Usuario;
import br.com.cesar.gestaoCondominial.dominio.dominium.usuario.repository.UsuarioRepository;
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
