package br.com.cesar.gestaoCondominial.comunicacao.dominio.notificacao;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@EqualsAndHashCode(of = "id")
public class Notificacao {

    private NotificacaoId id;
    private Long usuarioId;
    private String mensagem;
    private TipoNotificacao tipo;
    private boolean lida;
    private LocalDateTime criadaEm;

    private Notificacao() {}

    /**
     * Cria uma nova notificacao ainda sem ID (antes de persistir).
     */
    public static Notificacao criar(Long usuarioId, String mensagem, TipoNotificacao tipo) {
        Notificacao n = new Notificacao();
        n.usuarioId = usuarioId;
        n.mensagem = mensagem;
        n.tipo = tipo;
        n.lida = false;
        n.criadaEm = LocalDateTime.now();
        return n;
    }

    /**
     * Reconstitui uma notificacao carregada do banco de dados (com ID já gerado).
     */
    public static Notificacao reconstituir(NotificacaoId id, Long usuarioId, String mensagem,
                                           TipoNotificacao tipo, boolean lida, LocalDateTime criadaEm) {
        Notificacao n = new Notificacao();
        n.id = id;
        n.usuarioId = usuarioId;
        n.mensagem = mensagem;
        n.tipo = tipo;
        n.lida = lida;
        n.criadaEm = criadaEm;
        return n;
    }

    public void marcarComoLida() {
        this.lida = true;
    }
}
