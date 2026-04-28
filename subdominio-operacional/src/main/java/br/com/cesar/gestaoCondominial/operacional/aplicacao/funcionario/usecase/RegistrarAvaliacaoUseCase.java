package br.com.cesar.gestaoCondominial.operacional.aplicacao.funcionario.usecase;

import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.AvaliacaoFuncionario;
import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.FuncionarioId;
import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.repository.AvaliacaoFuncionarioRepository;
import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.repository.FuncionarioRepository;
import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.DomainException;
import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.ResourceNotFoundException;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.TipoUsuario;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.Usuario;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.repository.UsuarioRepository;
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
