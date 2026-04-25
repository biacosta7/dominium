package com.dominium.backend.application.assembleia.usecase;

import com.dominium.backend.domain.assembleia.Assembleia;
import com.dominium.backend.domain.assembleia.AssembleiaId;
import com.dominium.backend.domain.assembleia.repository.AssembleiaRepository;
import com.dominium.backend.domain.assembleia.service.ServicoNotificacaoAssembleia;
import com.dominium.backend.domain.shared.exceptions.DomainException;
import com.dominium.backend.domain.usuario.TipoUsuario;
import com.dominium.backend.domain.usuario.Usuario;
import com.dominium.backend.domain.usuario.repository.UsuarioRepository;
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
                AssembleiaId.novo(),
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
