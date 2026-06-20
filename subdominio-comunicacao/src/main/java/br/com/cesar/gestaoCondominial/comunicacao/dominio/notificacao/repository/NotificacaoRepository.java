package br.com.cesar.gestaoCondominial.comunicacao.dominio.notificacao.repository;

import br.com.cesar.gestaoCondominial.comunicacao.dominio.notificacao.Notificacao;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.notificacao.NotificacaoId;
import java.util.List;
import java.util.Optional;

public interface NotificacaoRepository {
    Notificacao save(Notificacao n);
    Optional<Notificacao> findById(NotificacaoId id);
    List<Notificacao> findByUsuarioId(Long usuarioId);
    List<Notificacao> findAll();
}
