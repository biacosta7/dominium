package com.dominium.backend.domain.usuario;

import java.util.Objects;

public class UsuarioId {

    private final Long valor;

    public UsuarioId(Long valor) {
        if (valor == null) {
            throw new IllegalArgumentException("ID do usuário não pode ser nulo");
        }
        this.valor = valor;
    }

    public Long getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UsuarioId)) return false;
        UsuarioId that = (UsuarioId) o;
        return Objects.equals(valor, that.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public String toString() {
        return String.valueOf(valor);
    }
}