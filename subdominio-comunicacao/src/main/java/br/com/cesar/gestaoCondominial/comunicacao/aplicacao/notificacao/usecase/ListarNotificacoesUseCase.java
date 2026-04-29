package br.com.cesar.gestaoCondominial.comunicacao.aplicacao.notificacao.usecase;

import br.com.cesar.gestaoCondominial.comunicacao.dominio.notificacao.Notificacao;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.notificacao.repository.NotificacaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarNotificacoesUseCase {

    private final NotificacaoRepository notificacaoRepository;

    public ListarNotificacoesUseCase(NotificacaoRepository notificacaoRepository) {
        this.notificacaoRepository = notificacaoRepository;
    }

    public List<Notificacao> executar(Long usuarioId) {
        return notificacaoRepository.findByUsuarioId(usuarioId);
    }
}
