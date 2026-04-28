package br.com.cesar.gestaoCondominial.governanca.aplicacao.assembleia.usecase;

import br.com.cesar.gestaoCondominial.governanca.dominio.assembleia.Assembleia;
import br.com.cesar.gestaoCondominial.governanca.dominio.assembleia.AssembleiaId;
import br.com.cesar.gestaoCondominial.governanca.dominio.assembleia.repository.AssembleiaRepository;
import br.com.cesar.gestaoCondominial.governanca.dominio.assembleia.service.ServicoNotificacaoAssembleia;
import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.DomainException;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.TipoUsuario;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.Usuario;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CriarAssembleiaUseCase {

    private final AssembleiaRepository assembleiaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ServicoNotificacaoAssembleia notificacao;

    public CriarAssembleiaUseCase(
            AssembleiaRepository assembleiaRepository,
            UsuarioRepository usuarioRepository,
            ServicoNotificacaoAssembleia notificacao
    ) {
        this.assembleiaRepository = assembleiaRepository;
        this.usuarioRepository = usuarioRepository;
        this.notificacao = notificacao;
    }

    @Transactional
    public Assembleia executar(Long sindicoId, String titulo, LocalDateTime dataHora, String local, List<String> pauta) {
        Usuario sindico = usuarioRepository.findById(sindicoId)
                .orElseThrow(() -> new DomainException("Usuário não encontrado"));

        if (sindico.getTipo() != TipoUsuario.SINDICO) {
            throw new DomainException("Apenas o síndico pode criar assembleias");
        }

        Assembleia assembleia = Assembleia.criar(
                new AssembleiaId(null),
                titulo,
                dataHora,
                local,
                pauta,
                sindicoId
        );

        Assembleia salva = assembleiaRepository.save(assembleia);
        notificacao.notificarMoradores(salva);
        return salva;
    }
}
