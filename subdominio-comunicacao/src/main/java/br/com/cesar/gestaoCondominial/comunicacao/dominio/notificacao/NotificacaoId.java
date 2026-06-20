package br.com.cesar.gestaoCondominial.comunicacao.dominio.notificacao;

import java.util.Objects;

/**
 * Value Object que representa a identidade de uma Notificacao.
 * Imutável, validado na criação, com igualdade por valor.
 */
public class NotificacaoId {

    private final Long valor;

    private NotificacaoId(Long valor) {
        if (valor == null || valor <= 0) {
            throw new IllegalArgumentException("ID de notificação inválido: " + valor);
        }
        this.valor = valor;
    }

    public static NotificacaoId de(Long valor) {
        return new NotificacaoId(valor);
    }

    public Long getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NotificacaoId)) return false;
        NotificacaoId that = (NotificacaoId) o;
        return valor.equals(that.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public String toString() {
        return valor.toString();
    }
}
