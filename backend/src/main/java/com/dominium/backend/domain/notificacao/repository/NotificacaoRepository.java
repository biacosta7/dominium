package com.dominium.backend.domain.notificacao.repository;

import com.dominium.backend.domain.notificacao.Notificacao;

import java.util.List;
import java.util.Optional;

public interface NotificacaoRepository {
    Notificacao save(Notificacao notificacao);
    Optional<Notificacao> findById(Long id);
    List<Notificacao> findByUsuarioId(Long usuarioId);
}
