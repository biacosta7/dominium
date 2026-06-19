package br.com.cesar.gestaoCondominial.operacional.aplicacao.funcionario.usecase;

import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.DomainException;
import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.ResourceNotFoundException;
import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.Funcionario;
import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.FuncionarioId;
import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.TipoVinculo;
import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.repository.FuncionarioRepository;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.TipoUsuario;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class EditarFuncionarioUseCase {

    private final FuncionarioRepository funcionarioRepository;
    private final UsuarioRepository usuarioRepository;

    public EditarFuncionarioUseCase(FuncionarioRepository funcionarioRepository, UsuarioRepository usuarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Funcionario executar(Long sindicoId, String id, String nome, String cpf, String email,
                                String telefone, TipoVinculo tipoVinculo,
                                LocalDate contratoInicio, LocalDate contratoFim, BigDecimal valorMensal) {
        var sindico = usuarioRepository.findById(sindicoId)
                .orElseThrow(() -> new DomainException("Usuário não encontrado"));

        if (sindico.getTipo() != TipoUsuario.SINDICO) {
            throw new DomainException("Apenas o síndico pode editar funcionários");
        }

        Funcionario funcionario = funcionarioRepository.findById(FuncionarioId.de(id))
                .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado com id: " + id));

        funcionario.editar(nome, cpf, email, telefone, tipoVinculo, contratoInicio, contratoFim, valorMensal);

        return funcionarioRepository.save(funcionario);
    }
}
