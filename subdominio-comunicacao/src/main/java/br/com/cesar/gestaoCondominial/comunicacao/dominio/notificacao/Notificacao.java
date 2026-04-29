package br.com.cesar.gestaoCondominial.comunicacao.dominio.notificacao;

import br.com.cesar.gestaoCondominial.comunicacao.dominio.notificacao.TipoNotificacao;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Notificacao {

    private Long id;
    private Long usuarioId;
    private String mensagem;
    private TipoNotificacao tipo;
    private boolean lida;
    private LocalDateTime criadaEm;

    public static Notificacao criar(Long usuarioId, String mensagem, TipoNotificacao tipo) {
        Notificacao n = new Notificacao();
        n.usuarioId = usuarioId;
        n.mensagem = mensagem;
        n.tipo = tipo;
        n.lida = false;
        n.criadaEm = LocalDateTime.now();
        return n;
    }

    public void marcarComoLida() {
        this.lida = true;
    }
}
