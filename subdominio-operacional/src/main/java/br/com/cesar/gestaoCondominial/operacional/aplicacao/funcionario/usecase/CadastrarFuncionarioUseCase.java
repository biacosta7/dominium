package br.com.cesar.gestaoCondominial.operacional.aplicacao.funcionario.usecase;

import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.Funcionario;
import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.FuncionarioId;
import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.TipoVinculo;
import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.repository.FuncionarioRepository;
import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.DomainException;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.TipoUsuario;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.Usuario;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class CadastrarFuncionarioUseCase {

    private final FuncionarioRepository funcionarioRepository;
    private final UsuarioRepository usuarioRepository;

    public CadastrarFuncionarioUseCase(FuncionarioRepository funcionarioRepository, UsuarioRepository usuarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Funcionario executar(Long sindicoId, String nome, String cpf, String email, String telefone,
                                TipoVinculo tipoVinculo, LocalDate contratoInicio, LocalDate contratoFim,
                                BigDecimal valorMensal) {
        Usuario sindico = usuarioRepository.findById(sindicoId)
                .orElseThrow(() -> new DomainException("Usuário não encontrado"));

        if (sindico.getTipo() != TipoUsuario.SINDICO) {
            throw new DomainException("Apenas o síndico pode cadastrar funcionários");
        }

        Funcionario funcionario = Funcionario.criar(
                FuncionarioId.novo(),
                nome, cpf, email, telefone,
                tipoVinculo, contratoInicio, contratoFim, valorMensal,
                sindicoId
        );

        return funcionarioRepository.save(funcionario);
    }
}
